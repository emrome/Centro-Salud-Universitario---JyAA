export interface GroupedCountDTO {
  group: string;     // bucket (ej: "20-29")
  subgroup: string;  // subgrupo (ej: "Mujer" | "Varón" | "N/D")
  count: number;
}
