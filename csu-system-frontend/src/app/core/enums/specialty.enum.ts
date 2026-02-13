export interface SpecialtyOption {
  value: string;
  label: string;
}

export const SPECIALTIES: SpecialtyOption[] = [
  { value: 'CLINIC', label: 'Clínica' },
  { value: 'PSYCHOLOGY', label: 'Psicología' },
  { value: 'NURSING', label: 'Enfermería' },
  { value: 'SOCIAL_WORK', label: 'Trabajo social' },
  { value: 'NUTRITION', label: 'Nutrición' }
];
