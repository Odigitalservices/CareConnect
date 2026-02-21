import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: 'professionals',
    loadComponent: () =>
      import('./features/professionals/professionals-list.component').then(
        (m) => m.ProfessionalsListComponent
      ),
  },
  { path: '', redirectTo: 'professionals', pathMatch: 'full' },
];
