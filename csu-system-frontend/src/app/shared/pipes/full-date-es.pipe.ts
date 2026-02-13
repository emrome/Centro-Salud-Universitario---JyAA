import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'fullDateEs',
  standalone: true
})
export class FullDateEsPipe implements PipeTransform {
  transform(dateStr: string): string {
    if (!dateStr) return '';

    const [year, month, day] = dateStr.split('-').map(Number);
    const date = new Date(year, month - 1, day);

    return new Intl.DateTimeFormat('es-AR', {
      weekday: 'long',
      day: 'numeric',
      month: 'long',
      year: 'numeric'
    }).format(date);
  }
}

