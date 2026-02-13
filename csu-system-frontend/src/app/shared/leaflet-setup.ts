import * as _L from 'leaflet';
(window as any).L = _L;
export const L = _L as any;

let drawLoaded: Promise<any> | null = null;
export function ensureLeafletDraw(): Promise<any> {
  if (!drawLoaded) drawLoaded = import('leaflet-draw');
  return drawLoaded;
}
