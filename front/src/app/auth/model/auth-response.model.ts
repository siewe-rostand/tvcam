import { UserModel } from "../../user/model/user.model";

export interface AuthResponseModel {
  token: string;
  fullname: string;
  telephone: string;
  userId: number;
  user?: UserModel
}
export interface LoginResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
}
