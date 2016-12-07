package presenter;

import model.InstructionBean;

public interface MainPresenter {
	void addInstruction(InstructionBean instructionBean);
        void clearData();
	void moveToNextCycle();
        void autoExcute();
        void pause();
}
