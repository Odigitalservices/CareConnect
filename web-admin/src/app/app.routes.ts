import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login.component';
import { inject } from '@angular/core';
import { Router } from '@angular/router';

const authGuard = () => {
  if (localStorage.getItem('access_token')) return true;
  return inject(Router).createUrlTree(['/login']);
};

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: 'professionals',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/professionals/professionals-list.component').then(
        (m) => m.ProfessionalsListComponent
      ),
  },
  { path: '', redirectTo: 'professionals', pathMatch: 'full' },
];
