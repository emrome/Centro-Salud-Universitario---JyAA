import { Component, Input, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { PersonFormBaseComponent } from '@features/admin/people/person/person-form-base/person-form-base.component';
import { DateUtils } from '@shared/utils/date-utils';

@Component({
  selector: 'app-user-form-base',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, PersonFormBaseComponent],
  templateUrl: './user-form-base.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserFormBaseComponent {
  @Input() form!: FormGroup;
  @Input() submitted = false;
  @Input() isEdit = false;

  maxBirthDate(): string {
    return DateUtils.isoDateTodayMinusYears(18);
  }

  get maxRegistrationDate(): string {
    return DateUtils.isoDateToday();
  }

  fieldInvalid(name: string): boolean {
    const c = this.form?.get(name);
    return !!c && c.invalid && (c.touched || c.dirty || this.submitted);
  }

  passwordVisible = false;

  togglePassword(): void {
    this.passwordVisible = !this.passwordVisible;
  }

  getError(name: string): string {
    const c = this.form?.get(name);
    if (!c || !c.errors) return '';

    const e = c.errors;
    if (e['required'])      return 'Este campo es obligatorio';
    if (e['email'])         return 'Email inválido';
    if (e['minlength'])     return `Mínimo ${e['minlength'].requiredLength} caracteres`;
    if (e['weakPassword'])  return 'Debe incluir mayúscula, minúscula y número';
    if (e['futureDate'])    return 'La fecha no puede ser en el futuro';
    return 'Dato inválido';
  }
}
