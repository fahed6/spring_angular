import { Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgIf, NgFor } from '@angular/common';
import { StaffService } from '../../services/staff.service';
import { Student } from '../../models/student.model';

@Component({
  selector: 'app-staff-students',
  standalone: true,
  imports: [FormsModule, NgIf, NgFor],
  templateUrl: './staff-students.component.html',
  styleUrl: './staff-students.component.css'
})
export class StaffStudentsComponent implements OnInit {
  private svc = inject(StaffService);
  students: Student[] = [];
  search = '';

  ngOnInit(): void { this.load(); }

  load(): void {
    this.svc.getStudents(this.search || undefined).subscribe(d => this.students = d);
  }

  statusClass(st: string): string {
    return { ACTIVE: 'bg-green-50 text-green-800 border-green-200', COMPLETED: 'bg-blue-50 text-blue-800 border-blue-200', SUSPENDED: 'bg-red-50 text-red-800 border-red-200' }[st] ?? 'bg-slate-50 text-slate-600 border-slate-200';
  }
}
