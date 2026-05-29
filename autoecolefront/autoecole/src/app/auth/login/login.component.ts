import { Component, inject } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { NgIf } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, NgIf],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  private fb     = inject(FormBuilder);
  private auth   = inject(AuthService);
  private router = inject(Router);

  form = this.fb.group({
    email:    ['', [Validators.required, Validators.email]],
    password: ['', Validators.required]
  });

  error   = '';
  loading = false;

  onLogin(): void {
    if (this.form.invalid) return;
    this.error   = '';
    this.loading = true;
    const { email, password } = this.form.value;
    this.auth.login(email!, password!).subscribe({
      next: () => {
        const dest = this.auth.isAdmin() ? '/admin/dashboard' : '/staff/dashboard';
        this.router.navigate([dest]);
      },
      error: () => { this.error = 'Email ou mot de passe incorrect.'; this.loading = false; }
    });
  }
}
