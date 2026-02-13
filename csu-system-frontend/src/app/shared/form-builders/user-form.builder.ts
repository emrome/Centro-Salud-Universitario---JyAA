import { Validators, FormBuilder } from '@angular/forms';
import { CustomValidators } from '@shared/validators/custom-validators';

export function buildUserFormGroup(fb: FormBuilder) {
  return fb.group({
    firstName:        ['', [Validators.required, Validators.minLength(2)]],
    lastName:         ['', [Validators.required, Validators.minLength(2)]],
    birthDate:        ['', [Validators.required, CustomValidators.minAge(18)]],
    email:            ['', [Validators.required, Validators.email]],
    password:         [''],
    registrationDate: ['', [Validators.required, CustomValidators.noFutureDate()]],
    enabled:          [true]
  });
}


