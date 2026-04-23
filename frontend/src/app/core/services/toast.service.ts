import { Injectable } from '@angular/core';
import Swal, { SweetAlertIcon, SweetAlertOptions } from 'sweetalert2';

@Injectable({
  providedIn: 'root',
})
export class ToastService {
  private readonly dedupeWindowMs = 1800;
  private readonly lastShown = new Map<string, number>();

  private readonly modalLayout: SweetAlertOptions = {
    customClass: {
      popup: 'swal2-fitness-popup',
      title: 'swal2-fitness-title',
      htmlContainer: 'swal2-fitness-body',
      actions: 'swal2-fitness-actions',
      confirmButton: 'swal2-fitness-confirm',
      cancelButton: 'swal2-fitness-cancel',
    },
    buttonsStyling: false,
    heightAuto: false,
  };

  success(message: string, title = 'Success!'): void {
    if (this.shouldSuppress('modal:success-inline', title, message)) return;
    void Swal.fire({
      ...this.modalLayout,
      icon: 'success',
      title,
      text: message,
      showConfirmButton: false,
      timer: 2200,
      timerProgressBar: true,
      allowOutsideClick: true,
      allowEscapeKey: true,
    });
  }

  error(message: string, title = 'Error'): void {
    this.openMessageModal('error', title, message, 'OK', 'modal:error');
  }

  warning(message: string, title = 'Warning'): void {
    this.openMessageModal('warning', title, message, 'OK', 'modal:warning');
  }

  info(message: string, title = 'Info'): void {
    this.openMessageModal('info', title, message, 'OK', 'modal:info');
  }

  criticalError(title: string, message: string): void {
    this.openMessageModal('error', title, message, 'OK', 'modal:critical');
  }

  confirm(
    title: string,
    text?: string,
    confirmText = 'Yes, proceed',
    cancelText = 'Cancel',
  ): Promise<boolean> {
    if (Swal.isVisible()) return Promise.resolve(false);
    return Swal.fire({
      ...this.modalLayout,
      icon: 'warning',
      title,
      text,
      showCancelButton: true,
      confirmButtonText: confirmText,
      cancelButtonText: cancelText,
      focusCancel: true,
      reverseButtons: true,
    }).then((result) => result.isConfirmed);
  }

  confirmAction(
    title: string,
    text?: string,
    confirmText = 'Yes',
    icon: SweetAlertIcon = 'question',
  ): Promise<boolean> {
    if (Swal.isVisible()) return Promise.resolve(false);
    return Swal.fire({
      ...this.modalLayout,
      icon,
      title,
      text,
      showCancelButton: true,
      confirmButtonText: confirmText,
      cancelButtonText: 'Cancel',
      reverseButtons: true,
    }).then((result) => result.isConfirmed);
  }

  validationError(title: string, errors: string[]): void {
    if (this.shouldSuppress('modal:validation', title, errors.join('|'))) return;
    const html = `<ul class="swal2-fitness-list">${errors
      .map((error) => `<li>${this.escapeHtml(error)}</li>`)
      .join('')}</ul>`;
    void Swal.fire({
      ...this.modalLayout,
      icon: 'warning',
      title,
      html,
      confirmButtonText: 'Fix it',
    });
  }

  successDialog(title: string, text?: string, confirmText = 'Continue'): Promise<boolean> {
    if (this.shouldSuppress('modal:success', title, text ?? '')) return Promise.resolve(false);
    return Swal.fire({
      ...this.modalLayout,
      icon: 'success',
      title,
      text,
      confirmButtonText: confirmText,
    }).then((res) => res.isConfirmed);
  }

  fire(options: SweetAlertOptions): Promise<import('sweetalert2').SweetAlertResult> {
    const merged = {
      ...(this.modalLayout as SweetAlertOptions),
      ...(options as SweetAlertOptions),
    } as SweetAlertOptions;
    return Swal.fire(merged);
  }

  private openMessageModal(
    icon: SweetAlertIcon,
    title: string,
    message: string,
    confirmText: string,
    keyPrefix: string,
  ): void {
    if (this.shouldSuppress(keyPrefix, title, message)) return;
    void Swal.fire({
      ...this.modalLayout,
      icon,
      title,
      text: message,
      confirmButtonText: confirmText,
    });
  }

  private shouldSuppress(type: string, title: string, message: string): boolean {
    const key = `${type}|${title}|${message}`;
    const now = Date.now();
    const last = this.lastShown.get(key) ?? 0;
    if (now - last < this.dedupeWindowMs) {
      return true;
    }
    this.lastShown.set(key, now);
    return false;
  }

  private escapeHtml(value: string): string {
    return value
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#039;');
  }
}
