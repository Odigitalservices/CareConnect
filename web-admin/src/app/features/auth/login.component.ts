import { Component } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [MatCardModule, MatButtonModule],
  template: `
    <div style="display:flex;justify-content:center;padding-top:100px">
      <mat-card style="width:400px;padding:32px">
        <mat-card-header>
          <mat-card-title>CareConnect Admin</mat-card-title>
          <mat-card-subtitle>Management Portal</mat-card-subtitle>
        </mat-card-header>
        <mat-card-content style="margin-top:24px">
          <p>Login form — implementation coming in next phase</p>
        </mat-card-content>
        <mat-card-actions>
          <button mat-raised-button color="primary">Login</button>
        </mat-card-actions>
      </mat-card>
    </div>
  `
})
export class LoginComponent {}
