export class HealthStaff {
  id?: number;
  firstName!: string;
  lastName!: string;
  birthDate!: string;
  email!: string;
  password?: string;
  registrationDate!: string;
  enabled!: boolean;
  specialty!: string;
  license!: string;
  deleted?: boolean;
}
