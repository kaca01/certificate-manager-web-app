import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { ConfigService } from './config.service';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { ResetPassword, User } from '../domains';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  public currentUser : User | null = null;

  private _expiredPassword = false;

  constructor(
    private apiService: ApiService,
    private config: ConfigService,
    private http: HttpClient) { }

  getMyInfo() {
    return this.apiService.get(this.config.current_user_url)
      .pipe(map(user => {
        this.currentUser = user;
        return user;
    }));
  }

  isExpiredPassword() : boolean {
    return this._expiredPassword;
  }

  setExpiredPassword(expiredPassword: boolean) {
    this._expiredPassword = expiredPassword;
  }

  checkLogin(user:any, radio: String, token: string) : Observable<any>{
    user.verification = radio;
    const headers = new HttpHeaders({
      'recaptcha': token,
      'Content-Type': 'application/json'
    });
    return this.http.post<any>(environment.apiHost + "api/user/checkLogin", user, {headers});
  }

  login(user:any, code: String) : Observable<any>{
    user.verification = code;
    return this.http.post<any>(environment.apiHost + "api/user/login", user);
  }

  register(user: any, verification: String, token: string): Observable<User> {
    user.verification = verification;
    const headers = new HttpHeaders({
      'recaptcha': token,
      'Content-Type': 'application/json'
    });
    return this.http.post<User>(environment.apiHost + 'api/user/register', user, {headers});
  }

  
  getActivation(activationId: number): Observable<String>  {
    return this.http.get<String>(environment.apiHost + "api/user/activate/" + activationId);
  }

  sendEmail(userEmail: string, token: string): Observable<any> {
    const headers = new HttpHeaders({
      'recaptcha': token,
      'Content-Type': 'application/json'
    });
    return this.http.get<any>(environment.apiHost + 'api/user/' + userEmail + "/resetPassword", {headers});
  }

  resetPasswordViaEmail(userEmail: string, resetPassword: ResetPassword): Observable<void> {
    return this.http.put<void>(environment.apiHost + 'api/user/' + userEmail + "/resetPassword", resetPassword);
  }

  sendSMS(phone: string, token: string): Observable<void> {
    const headers = new HttpHeaders({
      'recaptcha': token,
      'Content-Type': 'application/json'
    });
    return this.http.get<void>(environment.apiHost + 'api/user/' + phone + "/sendSMS", {headers});
  }

  resetPasswordViaSMS(phone: string, resetPassword: ResetPassword): Observable<void> {
    return this.http.put<void>(environment.apiHost + 'api/user/' + phone + "/sendSMS", resetPassword);
  }
}
