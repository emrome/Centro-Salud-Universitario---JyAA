import { Component, Input, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-person-form-base',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './person-form-base.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PersonFormBaseComponent {
  @Input() form!: FormGroup;
  @Input() submitted = false;
  @Input() maxDate?: string;

  fieldInvalid(name: string): boolean {
    const c = this.form?.get(name);
    return !!c && c.invalid && (c.touched || c.dirty || this.submitted);
  }

  getError(name: string): string {
    const c = this.form?.get(name);
    if (!c || !c.errors) return '';

    const e = c.errors;
    if (e['required'])  return 'Este campo es obligatorio';
    if (e['minlength']) return `Mínimo ${e['minlength'].requiredLength} caracteres`;
    if (e['underAge'])    return `Debe ser mayor de ${e['underAge'].minYears} años`;

    return 'Dato inválido';
  }
}
