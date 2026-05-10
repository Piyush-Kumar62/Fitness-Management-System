import { Component, HostListener, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-custom-cursor',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './custom-cursor.component.html',
  styleUrls: ['./custom-cursor.component.scss']
})
export class CustomCursorComponent implements OnInit {
  mouseX = signal(0);
  mouseY = signal(0);
  
  // Eased positions for the trailing circle
  trailingX = signal(0);
  trailingY = signal(0);

  isHovering = signal(false);
  isClicking = signal(false);

  private requestRef!: number;

  ngOnInit() {
    this.animate();
  }

  @HostListener('window:mousemove', ['$event'])
  onMouseMove(event: MouseEvent) {
    this.mouseX.set(event.clientX);
    this.mouseY.set(event.clientY);

    // Detect if hovering over clickable elements
    const target = event.target as HTMLElement;
    const isClickable = target.closest('a') || target.closest('button') || target.closest('input') || target.closest('select') || target.closest('.clickable');
    this.isHovering.set(!!isClickable);
  }

  @HostListener('window:mousedown')
  onMouseDown() {
    this.isClicking.set(true);
  }

  @HostListener('window:mouseup')
  onMouseUp() {
    this.isClicking.set(false);
  }

  // Animation loop for smooth trailing effect
  private animate = () => {
    const ease = 0.15; // Lower is smoother/slower, higher is faster
    
    const dx = this.mouseX() - this.trailingX();
    const dy = this.mouseY() - this.trailingY();
    
    this.trailingX.update(x => x + dx * ease);
    this.trailingY.update(y => y + dy * ease);
    
    this.requestRef = requestAnimationFrame(this.animate);
  }

  ngOnDestroy() {
    if (this.requestRef) {
      cancelAnimationFrame(this.requestRef);
    }
  }
}
