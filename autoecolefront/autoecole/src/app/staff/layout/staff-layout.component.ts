import { Component, inject } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-staff-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './staff-layout.component.html',
  styleUrl: './staff-layout.component.css'
})
export class StaffLayoutComponent {
  private auth = inject(AuthService);
  fullName     = this.auth.getFullName() ?? 'Staff';
  role         = this.auth.getRole() ?? '';

  logout(): void { this.auth.logout(); }
}
