import {AbstractControl, FormArray, ValidationErrors, ValidatorFn} from '@angular/forms';

export class CustomValidators {
  static minLengthArray(min: number): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (control instanceof FormArray) {
        return control.length >= min ? null : { minLengthArray: { requiredLength: min, actualLength: control.length } };
      }
      return { notFormArray: true };
    };
  }

  static minAge(minYears: number): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;
      if (!value) return null;
      const birth = new Date(value);
      if (Number.isNaN(birth.getTime())) return { invalidDate: true };

      const today = new Date();
      let age = today.getFullYear() - birth.getFullYear();
      const m = today.getMonth() - birth.getMonth();
      if (m < 0 || (m === 0 && today.getDate() < birth.getDate())) age--;

      return age >= minYears ? null : { underAge: { minYears, age } };
    };
  }

  static strongPassword(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;
      if (!value) return null;

      const hasUpperCase = /[A-Z]/.test(value);
      const hasLowerCase = /[a-z]/.test(value);
      const hasNumber    = /\d/.test(value);
      const hasSpecial   = /[!@#$%^&*()_\-+=[\]{};':"\\|,.<>/?]/.test(value);

      const isValid = hasUpperCase && hasLowerCase && hasNumber && hasSpecial;

      return isValid ? null : { weakPassword: true };
    };
  }

  static noFutureDate(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;
      if (!value) return null;

      const inputDate = new Date(value);
      const today = new Date();

      if (inputDate > today) {
        return { futureDate: true };
      }

      return null;
    };
  }
}
