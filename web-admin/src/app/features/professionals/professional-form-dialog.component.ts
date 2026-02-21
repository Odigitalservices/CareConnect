import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import {
  ProfessionalService,
  ProfessionalAdminSummary,
} from './professional.service';

export interface ProfessionalFormDialogData {
  professional?: ProfessionalAdminSummary;
}

@Component({
  selector: 'app-professional-form-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatProgressSpinnerModule,
  ],
  template: `
    <h2 mat-dialog-title>{{ isEdit ? 'Edit Professional' : 'Add Professional' }}</h2>
    <mat-dialog-content>
      <form [formGroup]="form" class="form-grid">
        @if (!isEdit) {
          <mat-form-field appearance="outline">
            <mat-label>Email</mat-label>
            <input matInput formControlName="email" type="email" />
            @if (form.get('email')?.hasError('required') && form.get('email')?.touched) {
              <mat-error>Email is required</mat-error>
            }
            @if (form.get('email')?.hasError('email')) {
              <mat-error>Invalid email format</mat-error>
            }
          </mat-form-field>
          <mat-form-field appearance="outline">
            <mat-label>Password</mat-label>
            <input matInput formControlName="password" type="password" />
            @if (form.get('password')?.hasError('minlength')) {
              <mat-error>Minimum 8 characters</mat-error>
            }
          </mat-form-field>
          <mat-form-field appearance="outline">
            <mat-label>First Name</mat-label>
            <input matInput formControlName="firstName" />
          </mat-form-field>
          <mat-form-field appearance="outline">
            <mat-label>Last Name</mat-label>
            <input matInput formControlName="lastName" />
          </mat-form-field>
          <mat-form-field appearance="outline">
            <mat-label>Phone</mat-label>
            <input matInput formControlName="phone" />
          </mat-form-field>
          <mat-form-field appearance="outline">
            <mat-label>License Number</mat-label>
            <input matInput formControlName="licenseNumber" />
          </mat-form-field>
        }
        <mat-form-field appearance="outline">
          <mat-label>Specialization</mat-label>
          <input matInput formControlName="specialization" />
          @if (form.get('specialization')?.hasError('required') && form.get('specialization')?.touched) {
            <mat-error>Specialization is required</mat-error>
          }
        </mat-form-field>
        <mat-form-field appearance="outline">
          <mat-label>City</mat-label>
          <input matInput formControlName="city" />
        </mat-form-field>
        <mat-form-field appearance="outline">
          <mat-label>Hourly Rate (MAD)</mat-label>
          <input matInput formControlName="hourlyRate" type="number" min="0" />
        </mat-form-field>
        <mat-form-field appearance="outline" style="grid-column: 1 / -1">
          <mat-label>Bio</mat-label>
          <textarea matInput formControlName="bio" rows="3"></textarea>
        </mat-form-field>
      </form>
      @if (errorMessage) {
        <p class="error-text">{{ errorMessage }}</p>
      }
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancel</button>
      <button mat-flat-button color="primary" [disabled]="saving" (click)="save()">
        @if (saving) { <mat-spinner diameter="18" /> } @else { Save }
      </button>
    </mat-dialog-actions>
  `,
  styles: [`
    .form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 0 16px; min-width: 480px; }
    .error-text { color: red; font-size: 0.875rem; }
  `],
})
export class ProfessionalFormDialogComponent {
  private readonly fb = inject(FormBuilder);
  private readonly service = inject(ProfessionalService);
  private readonly dialogRef = inject(MatDialogRef<ProfessionalFormDialogComponent>);
  readonly data: ProfessionalFormDialogData = inject(MAT_DIALOG_DATA);

  isEdit = !!this.data.professional;
  saving = false;
  errorMessage = '';

  form = this.fb.group({
    ...(this.isEdit
      ? {}
      : {
          email: [this.data.professional?.email ?? '', [Validators.required, Validators.email]],
          password: ['', [Validators.required, Validators.minLength(8)]],
          firstName: [this.data.professional?.firstName ?? '', Validators.required],
          lastName: [this.data.professional?.lastName ?? '', Validators.required],
          phone: [this.data.professional?.phone ?? ''],
          licenseNumber: [this.data.professional?.licenseNumber ?? '', Validators.required],
        }),
    specialization: [
      this.data.professional?.specialization ?? '',
      Validators.required,
    ],
    city: [this.data.professional?.city ?? ''],
    hourlyRate: [this.data.professional?.hourlyRate ?? null as number | null],
    bio: [this.data.professional?.bio ?? ''],
  });

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.saving = true;
    this.errorMessage = '';
    const v = this.form.value;

    const obs = this.isEdit
      ? this.service.update(this.data.professional!.id, {
          specialization: v.specialization ?? undefined,
          bio: v.bio || undefined,
          city: v.city || undefined,
          hourlyRate: v.hourlyRate ?? undefined,
        })
      : this.service.create({
          email: (v["email"] as unknown) as string,
          password: (v["password"] as unknown) as string,
          firstName: (v["firstName"] as unknown) as string,
          lastName: (v["lastName"] as unknown) as string,
          phone: (v["phone"] as unknown) as string || undefined,
          specialization: v.specialization!,
          licenseNumber: (v["licenseNumber"] as unknown) as string,
          city: v.city || undefined,
          hourlyRate: v.hourlyRate ?? undefined,
          bio: v.bio || undefined,
        });

    obs.subscribe({
      next: (result) => this.dialogRef.close(result),
      error: (err) => {
        this.errorMessage = err?.error?.message ?? 'An error occurred';
        this.saving = false;
      },
    });
  }
}
