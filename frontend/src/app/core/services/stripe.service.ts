import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { loadStripe, Stripe, StripeCardElement } from '@stripe/stripe-js';
import { environment } from '../../../environments/environment';

export interface CreatePaymentIntentRequest {
  planId: string;
  currency?: string;
}

export interface CreatePaymentIntentResponse {
  clientSecret: string;
  paymentIntentId: string;
  amount: number;
  currency: string;
  planName: string;
  planId: string;
}

@Injectable({ providedIn: 'root' })
export class StripeService {
  private stripePromise: Promise<Stripe | null>;
  private readonly apiBase = `${environment.apiUrl}/payments`;

  constructor(private http: HttpClient) {
    this.stripePromise = loadStripe(environment.stripePublishableKey);
  }

  // Step 1: Create a PaymentIntent on the backend and get the clientSecret
  createPaymentIntent(planId: string, currency = 'INR'): Observable<CreatePaymentIntentResponse> {
    const body: CreatePaymentIntentRequest = { planId, currency };
    return this.http.post<CreatePaymentIntentResponse>(`${this.apiBase}/create-intent`, body);
  }

  // Load the Stripe.js instance
  async getStripe(): Promise<Stripe | null> {
    return this.stripePromise;
  }

  // Step 2: Confirm the card payment on the Stripe side. Returns the PaymentIntent result (check .error for failures).
  async confirmCardPayment(
    clientSecret: string,
    cardElement: StripeCardElement,
    billingName: string,
  ) {
    const stripe = await this.getStripe();
    if (!stripe) throw new Error('Stripe.js failed to load');

    return stripe.confirmCardPayment(clientSecret, {
      payment_method: {
        card: cardElement,
        billing_details: { name: billingName },
      },
    });
  }
}
