export class SocialOrgRepresentative {
  id?: number;
  firstName!: string;
  lastName!: string;
  birthDate!: string;
  email!: string;
  password?: string;
  registrationDate!: string;
  enabled!: boolean;
  organizationId!: number;
  organizationName?: string;
  deleted?: boolean;
}
