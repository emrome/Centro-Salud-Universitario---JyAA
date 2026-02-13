export class Admin {
  id?: number;
  firstName!: string;
  lastName!: string;
  birthDate!: string;
  email!: string;
  password?: string;
  registrationDate!: string;
  enabled!: boolean;
  positionInCSU!: string;
  deleted?: boolean;
}
