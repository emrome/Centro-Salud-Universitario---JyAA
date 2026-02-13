export interface Survey {
  id: number;
  name: string;
  uploadDate: string;
}

export interface QuestionAnswer {
  question: Question;
  answers: Answer[];
}

export interface Question {
  code: string;
  text: string;
}

export interface Answer {
  answer: string;
}

