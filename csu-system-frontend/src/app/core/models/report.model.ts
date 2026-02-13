export interface Report {
  id?: number;
  name: string;
  description: string;
  authorId: number;
  authorName?: string;
  visibleToAllHealthStaff: boolean;
  publicVisible?: boolean;
  createdDate?: string;
  sharedWithIds: number[];
  hasFile: boolean;
  fileName?: string;
  fileMime?: string;
  downloadUrl?: string;
}
