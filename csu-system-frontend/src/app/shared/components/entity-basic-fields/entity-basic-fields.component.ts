import { Component, Input } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-entity-basic-fields',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './entity-basic-fields.component.html'
})
export class EntityBasicFieldsComponent {
  @Input({ required: true }) formGroup!: FormGroup;
}
