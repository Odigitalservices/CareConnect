import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiService } from '../../core/services/api.service';

export interface PagedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface ProfessionalAdminSummary {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  phone: string | null;
  specialization: string;
  licenseNumber: string;
  bio: string | null;
  city: string | null;
  hourlyRate: number | null;
  verified: boolean;
  createdAt: string;
}

export interface CreateProfessionalDto {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phone?: string;
  specialization: string;
  licenseNumber: string;
  city?: string;
  hourlyRate?: number;
  bio?: string;
}

export interface UpdateProfessionalDto {
  specialization?: string;
  bio?: string;
  city?: string;
  hourlyRate?: number;
}

@Injectable({ providedIn: 'root' })
export class ProfessionalService {
  private readonly api = inject(ApiService);

  list(page = 0, size = 20): Observable<PagedResponse<ProfessionalAdminSummary>> {
    return this.api
      .get<PagedResponse<ProfessionalAdminSummary>>(
        `/api/admin/professionals?page=${page}&size=${size}`
      )
      .pipe(map((r) => r.data));
  }

  create(data: CreateProfessionalDto): Observable<ProfessionalAdminSummary> {
    return this.api
      .post<ProfessionalAdminSummary>('/api/admin/professionals', data)
      .pipe(map((r) => r.data));
  }

  update(id: string, data: UpdateProfessionalDto): Observable<ProfessionalAdminSummary> {
    return this.api
      .put<ProfessionalAdminSummary>(`/api/admin/professionals/${id}`, data)
      .pipe(map((r) => r.data));
  }

  delete(id: string): Observable<void> {
    return this.api
      .delete<void>(`/api/admin/professionals/${id}`)
      .pipe(map(() => undefined));
  }

  toggleVerify(id: string): Observable<ProfessionalAdminSummary> {
    return this.api
      .patch<ProfessionalAdminSummary>(`/api/admin/professionals/${id}/verify`, {})
      .pipe(map((r) => r.data));
  }
}
