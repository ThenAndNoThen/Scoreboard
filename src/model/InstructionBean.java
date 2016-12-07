package model;

public class InstructionBean {
	public final static String LD="LD";
	public final static String MULTD="MULTD";
	public final static String SUBD="SUBD";
	public final static String DIVD="DIVD";
	public final static String ADDD="ADDD";
	public static int INSTRUCTION_COUNT=0;
	public final static int STAGES_NUM=4;
	
	private int    mInstructionID;
	private String mInstructionType;
	private String mDestRegister;
	private String mSourceRegisterJ;
	private String mSourceRegisterK;
	private int[]  mStages=new int[STAGES_NUM];
	private int    mInstrucetionStatus;
	
	public InstructionBean(String instructionType,String destREgister,String sourceRegisterJ,String sourRegisterK){
		this.mInstructionID=INSTRUCTION_COUNT;
		INSTRUCTION_COUNT++;
		this.mInstructionType=instructionType;
		this.mDestRegister=destREgister;
		this.mSourceRegisterJ=sourceRegisterJ;
		this.mSourceRegisterK=sourRegisterK;
		for(int i=0;i<STAGES_NUM;i++){
			this.mStages[i]=0;
		}
		this.mInstrucetionStatus=-1;
	}
	
	public int getmInstructionID() {
		return mInstructionID;
	}
	
	public String getmInstructionType() {
		return mInstructionType;
	}
	
	public String getmDestRegister() {
		return mDestRegister;
	}
	
	public String getmSourceRegisterJ() {
		return mSourceRegisterJ;
	}
	
	public String getmSourceRegisterK() {
		return mSourceRegisterK;
	}
	
	public int getmStageAt(int i) {
		return mStages[i];
	}
	
	public int getmInstrucetionStatus() {
		return mInstrucetionStatus;
	}
	
	
	public void setmStageAt(int stage,int cycle) {
		this.mStages[stage]=cycle;
	}
	
	public void setmInstrucetionStatus(int mInstrucetionStatus) {
		this.mInstrucetionStatus = mInstrucetionStatus;
	}
	
}
