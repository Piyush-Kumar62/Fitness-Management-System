import { Component, OnInit, OnDestroy, Input, Output, EventEmitter, ElementRef, ViewChild, AfterViewInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { StripeService, CreatePaymentIntentResponse } from '../../core/services/stripe.service';
import { ToastService } from '../../core/services/toast.service';
import { StripeElements, StripeCardElement } from '@stripe/stripe-js';

@Component({
  selector: 'app-stripe-checkout',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './stripe-checkout.component.html',
  styleUrls: ['./stripe-checkout.component.scss'],
})
export class StripeCheckoutComponent implements OnInit, AfterViewInit, OnDestroy {
  private fb = inject(FormBuilder);
  private stripeService = inject(StripeService);
  private toast = inject(ToastService);


  // The membership plan ID to purchase
  @Input() planId!: string;
  // Plan display name (shown in the UI)
  @Input() planName = '';
  // Amount to display (INR)
  @Input() planPrice = 0;

  // Fired when payment succeeds – parent can navigate away
  @Output() paymentSuccess = new EventEmitter<void>();
  // Fired when user clicks Cancel / Back
  @Output() cancelled = new EventEmitter<void>();

  @ViewChild('cardElementRef') cardElementRef!: ElementRef;

  form!: FormGroup;
  isLoading = false;
  intentReady = false;
  cardError = '';

  private cardElement: StripeCardElement | null = null;
  private stripeElements: StripeElements | null = null;
  private paymentIntentData: CreatePaymentIntentResponse | null = null;

  ngOnInit(): void {
    this.form = this.fb.group({
      cardHolderName: ['', [Validators.required, Validators.minLength(2)]],
    });
  }

  async ngAfterViewInit(): Promise<void> {
    await this.initStripe();
  }

  private async initStripe(): Promise<void> {
    const stripe = await this.stripeService.getStripe();
    if (!stripe) {
      this.toast.error('Stripe.js failed to load. Please refresh and try again.');
      return;
    }

    this.stripeElements = stripe.elements();
    this.cardElement = this.stripeElements.create('card', {
      style: {
        base: {
          color: '#e2e8f0',
          fontFamily: '"Segoe UI", Helvetica, sans-serif',
          fontSmoothing: 'antialiased',
          fontSize: '16px',
          '::placeholder': { color: '#4a5568' },
          backgroundColor: 'transparent',
        },
        invalid: { color: '#fc8181', iconColor: '#fc8181' },
      },
    });

    this.cardElement.mount(this.cardElementRef.nativeElement);

    this.cardElement.on('change', (event) => {
      this.cardError = event.error ? event.error.message : '';
    });

    // Pre-fetch the PaymentIntent so the user doesn't wait on "Pay Now"
    await this.fetchPaymentIntent();
  }

  private async fetchPaymentIntent(): Promise<void> {
    this.isLoading = true;
    this.stripeService.createPaymentIntent(this.planId, 'INR').subscribe({
      next: (data) => {
        this.paymentIntentData = data;
        this.intentReady = true;
        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;
        this.toast.error('Could not initialize payment. Please try again.');
      },
    });
  }

  async onSubmit(): Promise<void> {
    if (this.form.invalid) {
      this.toast.warning('Please enter the cardholder name.');
      return;
    }
    if (!this.cardElement || !this.paymentIntentData) {
      this.toast.error('Payment not ready. Please wait a moment and try again.');
      return;
    }
    if (this.cardError) {
      this.toast.warning('Please fix the card error before submitting.');
      return;
    }

    this.isLoading = true;
    const name = this.form.value.cardHolderName;

    try {
      const result = await this.stripeService.confirmCardPayment(
        this.paymentIntentData.clientSecret,
        this.cardElement,
        name,
      );

      if (result.error) {
        this.toast.error(`Payment failed: ${result.error.message ?? 'Unknown error'}`);
      } else if (result.paymentIntent?.status === 'succeeded') {
        await this.toast.successDialog(
          'Payment Successful! 🎉',
          `Your <strong>${this.planName}</strong> membership is now active. You'll receive a confirmation email shortly.`,
        );
        this.paymentSuccess.emit();
      } else {
        this.toast.info(`Payment status: ${result.paymentIntent?.status}. Please wait.`);
      }
    } catch (err: any) {
      this.toast.error(`Unexpected error: ${err?.message ?? 'Please try again.'}`);
    } finally {
      this.isLoading = false;
    }
  }

  onCancel(): void {
    this.cancelled.emit();
  }

  ngOnDestroy(): void {
    this.cardElement?.destroy();
  }
}
