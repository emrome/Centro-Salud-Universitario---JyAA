export interface OccupationOption {
  value: string;
  label: string;
}

export const OCCUPATIONS: OccupationOption[] = [
  { value: 'STUDENT', label: 'Estudiante' },
  { value: 'PROFESSIONAL', label: 'Profesional' },
  { value: 'CSU_STAFF', label: 'Personal CSU' },
  { value: 'VOLUNTEER', label: 'Voluntario' },
  { value: 'OTHER', label: 'Otro' }
];
