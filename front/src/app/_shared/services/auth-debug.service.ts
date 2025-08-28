import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { StorageService } from './storage.service';

export interface AuthDebugInfo {
    hasToken: boolean;
    tokenValid: boolean;
    tokenExpiry: Date | null;
    user: any;
    minutesUntilExpiry: number | null;
    isAuthenticated: boolean;
    timestamp: Date;
}

@Injectable({
    providedIn: 'root'
})
export class AuthDebugService {

    private debugInfo$ = new BehaviorSubject<AuthDebugInfo>(this.generateDebugInfo());

    constructor(private storageService: StorageService) {
        // Start monitoring in development mode
        this.startMonitoring();
    }

    getDebugInfo() {
        return this.debugInfo$.asObservable();
    }

    getCurrentDebugInfo(): AuthDebugInfo {
        return this.generateDebugInfo();
    }

    private generateDebugInfo(): AuthDebugInfo {
        const token = this.storageService.getToken;
        const tokenValid = this.storageService.isTokenValid();
        const user = this.storageService.getUser();
        const expirationStr = localStorage.getItem('tokenExpiration');

        let tokenExpiry: Date | null = null;
        let minutesUntilExpiry: number | null = null;

        if (expirationStr) {
            tokenExpiry = new Date(expirationStr);
            const now = new Date();
            minutesUntilExpiry = Math.round((tokenExpiry.getTime() - now.getTime()) / (1000 * 60));
        }

        return {
            hasToken: !!token,
            tokenValid,
            tokenExpiry,
            user,
            minutesUntilExpiry,
            isAuthenticated: tokenValid && !!user,
            timestamp: new Date()
        };
    }

    private startMonitoring() {
        // Update debug info every 30 seconds
        setInterval(() => {
            const debugInfo = this.generateDebugInfo();
            this.debugInfo$.next(debugInfo);

            // Log warning if token expires soon
            if (debugInfo.minutesUntilExpiry !== null && debugInfo.minutesUntilExpiry < 5 && debugInfo.minutesUntilExpiry > 0) {
                console.warn(`🚨 AUTH WARNING: Token expires in ${debugInfo.minutesUntilExpiry} minutes`);
            }

            // Log if token is expired but still present
            if (debugInfo.hasToken && !debugInfo.tokenValid) {
                console.error('🚨 AUTH ERROR: Token present but invalid/expired');
            }

        }, 30000);
    }

    logCurrentState() {
        const info = this.getCurrentDebugInfo();

        console.group('🔍 AUTH DEBUG INFO');
        console.log('Timestamp:', info.timestamp.toLocaleString());
        console.log('Has Token:', info.hasToken);
        console.log('Token Valid:', info.tokenValid);
        console.log('Is Authenticated:', info.isAuthenticated);

        if (info.tokenExpiry) {
            console.log('Token Expires:', info.tokenExpiry.toLocaleString());
            console.log('Minutes Until Expiry:', info.minutesUntilExpiry);
        }

        if (info.user) {
            console.log('User:', {
                telephone: info.user.telephone,
                firstname: info.user.firstname,
                lastname: info.user.lastname
            });
        }

        console.groupEnd();

        return info;
    }

    checkTokenStructure() {
        const token = this.storageService.getToken;

        if (!token) {
            console.error('🚨 No token found');
            return null;
        }

        try {
            const parts = token.split('.');
            if (parts.length !== 3) {
                console.error('🚨 Invalid JWT structure - should have 3 parts separated by dots');
                return null;
            }

            const header = JSON.parse(atob(parts[0]));
            const payload = JSON.parse(atob(parts[1]));

            console.group('🔍 TOKEN STRUCTURE');
            console.log('Header:', header);
            console.log('Payload:', payload);

            if (payload.exp) {
                const expDate = new Date(payload.exp * 1000);
                console.log('Token JWT expiry:', expDate.toLocaleString());
                console.log('Time until JWT expiry:', Math.round((expDate.getTime() - Date.now()) / 1000 / 60), 'minutes');
            }

            console.groupEnd();

            return { header, payload };
        } catch (error) {
            console.error('🚨 Error parsing token:', error);
            return null;
        }
    }

    simulateTokenExpiry() {
        console.warn('🧪 SIMULATING TOKEN EXPIRY');
        localStorage.setItem('tokenExpiration', new Date(Date.now() - 1000).toString());
        console.log('Token expiration set to 1 second ago');
    }

    resetAuth() {
        console.warn('🧪 RESETTING AUTHENTICATION');
        localStorage.removeItem('auth_token');
        localStorage.removeItem('tokenExpiration');
        localStorage.removeItem('auth_user');
        console.log('Authentication data cleared');
    }

    createTestToken(minutesValid: number = 60) {
        console.warn(`🧪 CREATING TEST TOKEN (valid for ${minutesValid} minutes)`);

        const expiryDate = new Date(Date.now() + minutesValid * 60 * 1000);
        const testUser = {
            telephone: 'test-user',
            firstname: 'Test',
            lastname: 'User',
            email: 'test@example.com'
        };

        // Create a fake JWT-like token for testing
        const header = btoa(JSON.stringify({ typ: 'JWT', alg: 'HS256' }));
        const payload = btoa(JSON.stringify({
            sub: 'test-user',
            exp: Math.floor(expiryDate.getTime() / 1000),
            iat: Math.floor(Date.now() / 1000)
        }));
        const testToken = `${header}.${payload}.test-signature`;

        localStorage.setItem('auth_token', testToken);
        localStorage.setItem('tokenExpiration', expiryDate.toString());
        localStorage.setItem('auth_user', JSON.stringify(testUser));

        console.log('Test token created and stored');
        console.log('Expires at:', expiryDate.toLocaleString());
    }
}
