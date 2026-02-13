import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SocialOrgRepresentativeListComponent } from './social-org-representative-list.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';

describe('SocialOrgRepresentativeListComponent', () => {
  let component: SocialOrgRepresentativeListComponent;
  let fixture: ComponentFixture<SocialOrgRepresentativeListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SocialOrgRepresentativeListComponent, HttpClientTestingModule, RouterTestingModule]
    }).compileComponents();

    fixture = TestBed.createComponent(SocialOrgRepresentativeListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
