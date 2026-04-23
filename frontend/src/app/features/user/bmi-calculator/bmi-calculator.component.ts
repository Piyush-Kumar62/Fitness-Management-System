import { Component, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

interface BMIResult {
  bmi: number;
  category: string;
  color: string;
  healthStatus: string;
  recommendations: string[];
}

@Component({
  selector: 'app-bmi-calculator',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './bmi-calculator.component.html',
})
export class BmiCalculatorComponent {
  unit = 'metric';
  weight: number | null = null;
  height: number | null = null;
  result = signal<BMIResult | null>(null);

  calculateBMI() {
    if (!this.weight || !this.height) {
      return;
    }

    let bmi: number;

    if (this.unit === 'metric') {
      // BMI = weight (kg) / (height (m))^2
      const heightInMeters = this.height / 100;
      bmi = this.weight / (heightInMeters * heightInMeters);
    } else {
      // BMI = (weight (lbs) / (height (in))^2) × 703
      bmi = (this.weight / (this.height * this.height)) * 703;
    }

    bmi = Math.round(bmi * 10) / 10;

    let category: string;
    let color: string;
    let healthStatus: string;
    let recommendations: string[];

    if (bmi < 18.5) {
      category = 'Underweight';
      color = '#3B82F6';
      healthStatus = 'You may need to gain weight';
      recommendations = [
        'Increase your calorie intake with nutrient-rich foods',
        'Include more protein in your diet',
        'Consider strength training exercises',
        'Consult a nutritionist for a personalized plan',
      ];
    } else if (bmi < 25) {
      category = 'Normal Weight';
      color = '#10B981';
      healthStatus = 'You are at a healthy weight';
      recommendations = [
        'Maintain your current healthy lifestyle',
        'Continue regular physical activity',
        'Eat a balanced diet',
        'Stay hydrated and get enough sleep',
      ];
    } else if (bmi < 30) {
      category = 'Overweight';
      color = '#F59E0B';
      healthStatus = 'You may benefit from losing weight';
      recommendations = [
        'Increase physical activity to 150+ minutes per week',
        'Focus on portion control',
        'Choose whole foods over processed foods',
        'Consider consulting a healthcare provider',
      ];
    } else {
      category = 'Obese';
      color = '#EF4444';
      healthStatus = 'Consult a healthcare provider';
      recommendations = [
        'Seek professional medical advice',
        'Create a structured weight loss plan',
        'Start with low-impact exercises',
        'Monitor your progress regularly',
        'Consider joining a support group',
      ];
    }

    this.result.set({
      bmi,
      category,
      color,
      healthStatus,
      recommendations,
    });
  }
}
