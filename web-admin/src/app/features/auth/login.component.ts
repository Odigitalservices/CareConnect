import { Component, inject } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../core/services/api.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule,
    MatProgressSpinnerModule,
  ],
  template: `
    <div style="display:flex;justify-content:center;padding-top:100px">
      <mat-card style="width:400px;padding:32px">
        <mat-card-header>
          <mat-card-title>CareConnect Admin</mat-card-title>
          <mat-card-subtitle>Management Portal</mat-card-subtitle>
        </mat-card-header>
        <mat-card-content style="margin-top:24px">
          <form [formGroup]="form" (ngSubmit)="login()" style="display:flex;flex-direction:column;gap:16px">
            <mat-form-field appearance="outline">
              <mat-label>Email</mat-label>
              <input matInput formControlName="email" type="email" autocomplete="email">
            </mat-form-field>
            <mat-form-field appearance="outline">
              <mat-label>Password</mat-label>
              <input matInput formControlName="password" type="password" autocomplete="current-password">
            </mat-form-field>
            <p *ngIf="error" style="color:red;margin:0">{{ error }}</p>
            <button mat-raised-button color="primary" type="submit" [disabled]="loading">
              <mat-spinner *ngIf="loading" diameter="20" style="display:inline-block;margin-right:8px"></mat-spinner>
              Login
            </button>
          </form>
        </mat-card-content>
      </mat-card>
    </div>
  `
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private api = inject(ApiService);
  private router = inject(Router);

  form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
  });

  loading = false;
  error = '';

  login() {
    if (this.form.invalid) return;
    this.loading = true;
    this.error = '';
    this.api.post<{ accessToken: string }>('/api/auth/login', this.form.value).subscribe({
      next: (res) => {
        localStorage.setItem('access_token', res.data.accessToken);
        this.router.navigate(['/professionals']);
      },
      error: () => {
        this.error = 'Invalid email or password';
        this.loading = false;
      },
    });
  }
}
