import { GenderIdentityOption } from '@core/enums/gender-identity.enum';
import { OccupationOption } from '@core/enums/occupation.enum';

export class Surveyor {
  id?: number;
  firstName!: string;
  lastName!: string;
  birthDate!: string;
  dni!: string;
  gender?: GenderIdentityOption;
  occupation?: OccupationOption;
}
