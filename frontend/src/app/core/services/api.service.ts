import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';

@Injectable({
  providedIn: 'root',
})
export class ApiService {
  private http = inject(HttpClient);
  private baseUrl = environment.apiUrl;

  // GET request
  get<T>(endpoint: string, params?: HttpParams | Record<string, any>): Observable<T> {
    const httpParams = params instanceof HttpParams ? params : this.createHttpParams(params);
    return this.http
      .get<T | ApiResponse<T>>(`${this.baseUrl}/${endpoint}`, { params: httpParams })
      .pipe(map((response) => this.unwrapResponse<T>(response)));
  }

  // POST request
  post<T>(endpoint: string, body: any, options?: { headers?: HttpHeaders }): Observable<T> {
    return this.http
      .post<T | ApiResponse<T>>(`${this.baseUrl}/${endpoint}`, body, options)
      .pipe(map((response) => this.unwrapResponse<T>(response)));
  }

  // PUT request
  put<T>(endpoint: string, body: any): Observable<T> {
    return this.http
      .put<T | ApiResponse<T>>(`${this.baseUrl}/${endpoint}`, body)
      .pipe(map((response) => this.unwrapResponse<T>(response)));
  }

  // PATCH request
  patch<T>(endpoint: string, body: any): Observable<T> {
    return this.http
      .patch<T | ApiResponse<T>>(`${this.baseUrl}/${endpoint}`, body)
      .pipe(map((response) => this.unwrapResponse<T>(response)));
  }

  // DELETE request
  delete<T>(endpoint: string): Observable<T> {
    return this.http
      .delete<T | ApiResponse<T>>(`${this.baseUrl}/${endpoint}`)
      .pipe(map((response) => this.unwrapResponse<T>(response)));
  }

  // Create HttpParams from object
  private createHttpParams(params?: Record<string, any>): HttpParams {
    let httpParams = new HttpParams();
    if (params) {
      Object.keys(params).forEach((key) => {
        const value = params[key];
        if (value !== null && value !== undefined) {
          httpParams = httpParams.set(key, String(value));
        }
      });
    }
    return httpParams;
  }

  private unwrapResponse<T>(response: T | ApiResponse<T>): T {
    if (
      response &&
      typeof response === 'object' &&
      'success' in (response as Record<string, unknown>)
    ) {
      const envelope = response as ApiResponse<T>;
      return (envelope.data as T) ?? ({} as T);
    }
    return response as T;
  }
}
