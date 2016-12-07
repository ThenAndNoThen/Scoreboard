package view;

import model.FunctionalUnit;
import model.InstructionBean;

public interface MainView {
	void setInstruction(InstructionBean instructionBean);
	void setUnit(FunctionalUnit functionalUnit);
	void setRegister(String register,String unit);
	void setCostTimeVisible(FunctionalUnit functionalUnit);
	void setCostTimeUnvisible(FunctionalUnit functionalUnit);
	void setCostTime(FunctionalUnit functionalUnit);
	void setPresnterCycle(int cycle);
        void showFinished();
        void addInstruction(InstructionBean instructionBean);
        void resetUI();
}
