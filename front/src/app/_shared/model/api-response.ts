export interface ApiResponse<T> {
  status: string;
  statusCode: number;
  message: string;
  data: T;
}

export interface DropdownOptionModel {
  id: string;
  label: string;
  icon: string;
}
