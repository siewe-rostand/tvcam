import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-simple-loading',
    standalone: true,
    imports: [CommonModule],
    template: `
    <div class="simple-loading-container">
      <div class="simple-loading-content">
        <div class="spinner"></div>
        <h2>📺 TV CAM</h2>
        <p>Chargement en cours...</p>
      </div>
    </div>
  `,
    styles: [`
    .simple-loading-container {
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      display: flex;
      justify-content: center;
      align-items: center;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      z-index: 10000;
    }
    
    .simple-loading-content {
      text-align: center;
      background: rgba(255, 255, 255, 0.95);
      padding: 3rem;
      border-radius: 20px;
      box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
    }
    
    .spinner {
      width: 50px;
      height: 50px;
      border: 4px solid #f3f3f3;
      border-top: 4px solid #667eea;
      border-radius: 50%;
      animation: spin 1s linear infinite;
      margin: 0 auto 1rem;
    }
    
    @keyframes spin {
      0% { transform: rotate(0deg); }
      100% { transform: rotate(360deg); }
    }
    
    h2 {
      color: #4a5568;
      margin: 0 0 0.5rem 0;
      font-weight: 700;
      font-size: 1.8rem;
    }
    
    p {
      color: #718096;
      margin: 0;
      font-size: 1rem;
      font-weight: 500;
    }
  `]
})
export class SimpleLoadingComponent { }
