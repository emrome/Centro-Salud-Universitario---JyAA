export interface GenderIdentityOption {
  value: string;
  label: string;
}

export const GENDER_IDENTITIES: GenderIdentityOption[] = [
  { value: 'WOMAN_CIS', label: 'Mujer cis' },
  { value: 'WOMAN_TRANS', label: 'Mujer trans / travesti' },
  { value: 'MAN_CIS', label: 'Varón cis' },
  { value: 'MAN_TRANS', label: 'Varón trans / masculinidad trans' },
  { value: 'NON_BINARY', label: 'No binarie' },
  { value: 'OTHER_IDENTITY', label: 'Otra identidad / ninguna de las anteriores' },
  { value: 'DONT_KNOW_OR_NO_ANSWER', label: 'No sabe o no contesta' }
];
