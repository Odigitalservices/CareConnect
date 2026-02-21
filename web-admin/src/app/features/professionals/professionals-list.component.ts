import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatTableModule } from '@angular/material/table';
import { MatToolbarModule } from '@angular/material/toolbar';
import {
  ProfessionalAdminSummary,
  ProfessionalService,
} from './professional.service';
import {
  ProfessionalFormDialogComponent,
  ProfessionalFormDialogData,
} from './professional-form-dialog.component';

@Component({
  selector: 'app-professionals-list',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatPaginatorModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatSlideToggleModule,
    MatToolbarModule,
  ],
  template: `
    <mat-toolbar color="primary">
      <span>Professionals</span>
      <span style="flex:1"></span>
      <button mat-raised-button (click)="openCreate()">Add Professional</button>
    </mat-toolbar>

    <div style="padding: 16px">
      <mat-table [dataSource]="professionals">
        <ng-container matColumnDef="name">
          <mat-header-cell *matHeaderCellDef>Name</mat-header-cell>
          <mat-cell *matCellDef="let p">{{ p.firstName }} {{ p.lastName }}</mat-cell>
        </ng-container>

        <ng-container matColumnDef="specialization">
          <mat-header-cell *matHeaderCellDef>Specialization</mat-header-cell>
          <mat-cell *matCellDef="let p">{{ p.specialization }}</mat-cell>
        </ng-container>

        <ng-container matColumnDef="city">
          <mat-header-cell *matHeaderCellDef>City</mat-header-cell>
          <mat-cell *matCellDef="let p">{{ p.city ?? '—' }}</mat-cell>
        </ng-container>

        <ng-container matColumnDef="rate">
          <mat-header-cell *matHeaderCellDef>Rate</mat-header-cell>
          <mat-cell *matCellDef="let p">{{ p.hourlyRate != null ? (p.hourlyRate + ' MAD/h') : '—' }}</mat-cell>
        </ng-container>

        <ng-container matColumnDef="verified">
          <mat-header-cell *matHeaderCellDef>Verified</mat-header-cell>
          <mat-cell *matCellDef="let p">
            <mat-slide-toggle [checked]="p.verified" (change)="toggleVerify(p)" />
          </mat-cell>
        </ng-container>

        <ng-container matColumnDef="actions">
          <mat-header-cell *matHeaderCellDef>Actions</mat-header-cell>
          <mat-cell *matCellDef="let p">
            <button mat-icon-button (click)="openEdit(p)">
              <mat-icon>edit</mat-icon>
            </button>
            <button mat-icon-button color="warn" (click)="confirmDelete(p)">
              <mat-icon>delete</mat-icon>
            </button>
          </mat-cell>
        </ng-container>

        <mat-header-row *matHeaderRowDef="columns"></mat-header-row>
        <mat-row *matRowDef="let row; columns: columns"></mat-row>
      </mat-table>

      <mat-paginator
        [length]="totalElements"
        [pageSize]="pageSize"
        [pageIndex]="pageIndex"
        [pageSizeOptions]="[10, 20, 50]"
        (page)="onPage($event)"
      />
    </div>
  `,
})
export class ProfessionalsListComponent implements OnInit {
  private readonly service = inject(ProfessionalService);
  private readonly dialog = inject(MatDialog);

  columns = ['name', 'specialization', 'city', 'rate', 'verified', 'actions'];
  professionals: ProfessionalAdminSummary[] = [];
  totalElements = 0;
  pageIndex = 0;
  pageSize = 20;

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.service.list(this.pageIndex, this.pageSize).subscribe((page) => {
      this.professionals = page.content;
      this.totalElements = page.totalElements;
    });
  }

  onPage(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.load();
  }

  openCreate(): void {
    const data: ProfessionalFormDialogData = {};
    this.dialog
      .open(ProfessionalFormDialogComponent, { data })
      .afterClosed()
      .subscribe((result) => { if (result) this.load(); });
  }

  openEdit(professional: ProfessionalAdminSummary): void {
    const data: ProfessionalFormDialogData = { professional };
    this.dialog
      .open(ProfessionalFormDialogComponent, { data })
      .afterClosed()
      .subscribe((result) => { if (result) this.load(); });
  }

  confirmDelete(professional: ProfessionalAdminSummary): void {
    if (!confirm(`Delete ${professional.firstName} ${professional.lastName}?`)) return;
    this.service.delete(professional.id).subscribe(() => this.load());
  }

  toggleVerify(professional: ProfessionalAdminSummary): void {
    this.service.toggleVerify(professional.id).subscribe((updated) => {
      const idx = this.professionals.findIndex((p) => p.id === updated.id);
      if (idx !== -1) this.professionals = [
        ...this.professionals.slice(0, idx),
        updated,
        ...this.professionals.slice(idx + 1),
      ];
    });
  }
}
