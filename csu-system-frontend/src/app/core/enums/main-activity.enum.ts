export interface MainActivityOption {
  value: string;
  label: string;
}

export const MAIN_ACTIVITIES: MainActivityOption[] = [
  { value: 'HEALTH', label: 'Salud' },
  { value: 'EDUCATION', label: 'Educación' },
  { value: 'SPORTS', label: 'Deportes' },
  { value: 'CULTURE', label: 'Cultura' },
  { value: 'SOCIAL_ASSISTANCE', label: 'Asistencia social' }
];
