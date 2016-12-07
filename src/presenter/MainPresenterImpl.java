package presenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import javax.swing.JOptionPane;

import model.FunctionalUnit;
import model.InstructionBean;
import view.MainView;

public class MainPresenterImpl implements MainPresenter{
	final static int FLOAT_REGISTER_NUM=16;
	
	List<InstructionBean> mInstructList;
	Map<String, FunctionalUnit> mFunctionalUnitMap;
	Map<String, String> mRegisterMap;
	List<InstructionBean> mWaitToNextStageList;
        FunctionalUnit mWaitToIssue;
	int mInstructionIssued;
	int mCycleCount;
        int mUnitIssuedThisCycle;
        boolean isFinished;
        Timer timer;
	MainView mMainView;
	public MainPresenterImpl(MainView mainView){
                mInstructList=new ArrayList<InstructionBean>();
		mWaitToNextStageList=new LinkedList<InstructionBean>();
		mInstructionIssued=0;
		mCycleCount=0;
                mUnitIssuedThisCycle=-1;
                isFinished=false;
                timer=null;
		mMainView=mainView;
		mFunctionalUnitMap=new HashMap<String, FunctionalUnit>();
		mFunctionalUnitMap.put(FunctionalUnit.INTEGER, new FunctionalUnit(FunctionalUnit.INTEGER));
		mFunctionalUnitMap.put(FunctionalUnit.MUTL1, new FunctionalUnit(FunctionalUnit.MUTL1));
		mFunctionalUnitMap.put(FunctionalUnit.MUTL2, new FunctionalUnit(FunctionalUnit.MUTL2));
		mFunctionalUnitMap.put(FunctionalUnit.ADD, new FunctionalUnit(FunctionalUnit.ADD));
		mFunctionalUnitMap.put(FunctionalUnit.DIVIDE, new FunctionalUnit(FunctionalUnit.DIVIDE));
		mRegisterMap=new HashMap<String,String>();
		for(int i=0;i<FLOAT_REGISTER_NUM;i++){
			mRegisterMap.put("F"+i*2, "");
		}
	}
	
	@Override
	public void addInstruction(InstructionBean instructionBean){
                if(isFinished==false && mCycleCount==0 ){
                    mInstructList.add(instructionBean);
                    mMainView.addInstruction(instructionBean);
                }
		
	}
	
	@Override
	public void moveToNextCycle(){
            if(isFinished){
                return;
            }
            mWaitToNextStageList.clear();
            mCycleCount++;
            mMainView.setPresnterCycle(mCycleCount);
            issue();
            otherProcess();
            for(int i=0;i<mWaitToNextStageList.size();i++){
                    InstructionBean instructionBean=mWaitToNextStageList.get(i);
                    instructionBean.setmInstrucetionStatus(instructionBean.getmInstrucetionStatus()+1);
            }
            if(mFunctionalUnitMap.get(FunctionalUnit.ADD).getmIsUnitBusy()==false && 
                    mFunctionalUnitMap.get(FunctionalUnit.DIVIDE).getmIsUnitBusy()==false &&
                    mFunctionalUnitMap.get(FunctionalUnit.INTEGER).getmIsUnitBusy()==false &&
                    mFunctionalUnitMap.get(FunctionalUnit.MUTL1).getmIsUnitBusy()==false &&
                    mFunctionalUnitMap.get(FunctionalUnit.MUTL2).getmIsUnitBusy()==false &&
                    mInstructionIssued==mInstructList.size()){
                isFinished=true;
                mMainView.showFinished();
                if(timer!=null){
                    timer.cancel();
                    timer=null;
                }
            }
		
	}
	
	private void issue(){
            if(mInstructionIssued==mInstructList.size()){
                mUnitIssuedThisCycle=-1;
                return;
            }
            InstructionBean instructionBean=mInstructList.get(mInstructionIssued);
            switch(instructionBean.getmInstructionType()){
            case InstructionBean.ADDD:
            case InstructionBean.SUBD:
                    FunctionalUnit addFunctionalUnit=mFunctionalUnitMap.get(FunctionalUnit.ADD);
                    updateFunctionalUnitStatus(addFunctionalUnit,instructionBean);
                    break;
            case InstructionBean.DIVD:
                    FunctionalUnit divdFunctionalUnit=mFunctionalUnitMap.get(FunctionalUnit.DIVIDE);
                    updateFunctionalUnitStatus(divdFunctionalUnit,instructionBean);
                    break;
            case InstructionBean.LD:
                    FunctionalUnit ldFunctionalUnit=mFunctionalUnitMap.get(FunctionalUnit.INTEGER);
                    updateFunctionalUnitStatus(ldFunctionalUnit,instructionBean);
                    break;
            case InstructionBean.MULTD:
                    FunctionalUnit mulFunctionalUnit=mFunctionalUnitMap.get(FunctionalUnit.MUTL1);
                    if(mulFunctionalUnit.getmIsUnitBusy()){
                            mulFunctionalUnit=mFunctionalUnitMap.get(FunctionalUnit.MUTL2);
                    }

                    updateFunctionalUnitStatus(mulFunctionalUnit,instructionBean);
                    break;
            }
	}
	
	private void updateFunctionalUnitStatus(FunctionalUnit functionalUnit,InstructionBean instructionBean){
		//�����Ҫ�ļĴ������У�����û��waw��أ��ͽ���issue
		if(functionalUnit.getmIsUnitBusy()==false && mRegisterMap.get(instructionBean.getmDestRegister()).equals("") ){
			
			instructionBean.setmStageAt(instructionBean.getmInstrucetionStatus()+1, mCycleCount);
			mMainView.setInstruction(instructionBean);
                        functionalUnit.setmIsUnitBusy(true);
                        instructionBean.setmInstrucetionStatus(instructionBean.getmInstrucetionStatus()+1);
			functionalUnit.setmInstructionBean(instructionBean);
			//��functionalunit���г�ʼ�����ж�j��k�Ĵ����Ƿ��Ѿ�׼�����������û�У�����Ҫ����д���unit���ĸ�
			updateUnitJKReady(functionalUnit);
			functionalUnit.setmTimeLeft(functionalUnit.getmTimeCost());
			//�������unit��Ҫ��Ŀ�ļĴ�������д��
			mRegisterMap.put(instructionBean.getmDestRegister(), functionalUnit.getmUnitName());
			mMainView.setUnit(functionalUnit);
                        mMainView.setRegister(instructionBean.getmDestRegister(), functionalUnit.getmUnitName());
			mInstructionIssued++;
                        mUnitIssuedThisCycle=functionalUnit.getmUnitNum();
		}
                else{mUnitIssuedThisCycle=-1;}
	}
	
	private void otherProcess(){
		FunctionalUnit functionalUnit=mFunctionalUnitMap.get(FunctionalUnit.INTEGER);
                if(functionalUnit!=null){
                    operands(functionalUnit);
                }
		
		functionalUnit=mFunctionalUnitMap.get(FunctionalUnit.ADD);
		 if(functionalUnit!=null){
                    operands(functionalUnit);
                }
		functionalUnit=mFunctionalUnitMap.get(FunctionalUnit.MUTL1);
		 if(functionalUnit!=null){
                    operands(functionalUnit);
                }
		functionalUnit=mFunctionalUnitMap.get(FunctionalUnit.MUTL2);
		 if(functionalUnit!=null){
                    operands(functionalUnit);
                }
		functionalUnit=mFunctionalUnitMap.get(FunctionalUnit.DIVIDE);
		 if(functionalUnit!=null){
                    operands(functionalUnit);
                }
		
	    functionalUnit=mFunctionalUnitMap.get(FunctionalUnit.INTEGER);
		processInstructionInUnit(functionalUnit);
		functionalUnit=mFunctionalUnitMap.get(FunctionalUnit.ADD);
		processInstructionInUnit(functionalUnit);
		functionalUnit=mFunctionalUnitMap.get(FunctionalUnit.MUTL1);
		processInstructionInUnit(functionalUnit);
		functionalUnit=mFunctionalUnitMap.get(FunctionalUnit.MUTL2);
		processInstructionInUnit(functionalUnit);
		functionalUnit=mFunctionalUnitMap.get(FunctionalUnit.DIVIDE);
		processInstructionInUnit(functionalUnit);
	}
	private void operands(FunctionalUnit functionalUnit){
                if(functionalUnit.getmIsUnitBusy()==false || functionalUnit.getmUnitNum()==mUnitIssuedThisCycle){
                    return;
                }
		InstructionBean instructionBean=functionalUnit.getmInstructionBean();
                
		switch(instructionBean.getmInstrucetionStatus()){
		//�ȴ�ȡ������
		case 0:
			//�������Դ������������ȡ
			if(functionalUnit.getmIsJReady() && functionalUnit.getmIsKReady()){
				instructionBean.setmStageAt(1, mCycleCount);
				mWaitToNextStageList.add(instructionBean);
				mMainView.setInstruction(instructionBean);
				mMainView.setCostTimeVisible(functionalUnit);
			}
			break;
		}
	}
	private void processInstructionInUnit(FunctionalUnit functionalUnit){
             if(functionalUnit.getmIsUnitBusy()==false){
                    return;
                }
		InstructionBean instructionBean=functionalUnit.getmInstructionBean();
		switch(instructionBean.getmInstrucetionStatus()){
		//�ȴ�����
		case 1:
			int mTimeLeft=functionalUnit.getmTimeLeft();
			mTimeLeft--;
			functionalUnit.setmTimeLeft(mTimeLeft);
			mMainView.setCostTime(functionalUnit);
			if(mTimeLeft==0){
				instructionBean.setmStageAt(2, mCycleCount);
				mWaitToNextStageList.add(instructionBean);
				mMainView.setInstruction(instructionBean);
			}
			break;
		//�ȴ�д��
		case 2:
			String destRegister=instructionBean.getmDestRegister();
			int warInstructid=getInstructionIdInUnit(FunctionalUnit.ADD,destRegister);
			if(warInstructid!=-1 && warInstructid<instructionBean.getmInstructionID()){
				return;
			}
                        warInstructid=getInstructionIdInUnit(FunctionalUnit.DIVIDE,destRegister);
			if(warInstructid!=-1 && warInstructid<instructionBean.getmInstructionID()){
				return;
			}
                        warInstructid=getInstructionIdInUnit(FunctionalUnit.INTEGER,destRegister);
			if(warInstructid!=-1 && warInstructid<instructionBean.getmInstructionID()){
				return;
			}
                        warInstructid=getInstructionIdInUnit(FunctionalUnit.MUTL1,destRegister);
			if(warInstructid!=-1 && warInstructid<instructionBean.getmInstructionID()){
				return;
			}
                        warInstructid=getInstructionIdInUnit(FunctionalUnit.MUTL2,destRegister);
			if(warInstructid!=-1 && warInstructid<instructionBean.getmInstructionID()){
				return;
			}
                        writeResult(functionalUnit);
			break;
			
		}
	}
	
	//�������unit�л�û��������������Դ�Ĵ����Ͳ�����Ŀ�ļĴ���һ�µ�ָ��id
	private int getInstructionIdInUnit(String unitName,String destRegister){
		int instructid=-1;
		FunctionalUnit functionalUnit=mFunctionalUnitMap.get(unitName);
		
		if(functionalUnit.getmIsUnitBusy()){
			InstructionBean instructionBean=functionalUnit.getmInstructionBean();
			if(instructionBean.getmInstrucetionStatus()==0 ){
				if(instructionBean.getmSourceRegisterJ().equals(destRegister) || instructionBean.getmSourceRegisterK().equals(destRegister)){
					return instructionBean.getmInstructionID();
				}
			}
		}
		return instructid;
	}
	
	private void writeResult(FunctionalUnit functionalUnit){
		mMainView.setCostTimeUnvisible(functionalUnit);
		InstructionBean instructionBean=functionalUnit.getmInstructionBean();
		mRegisterMap.put(instructionBean.getmDestRegister(), "");
		mMainView.setRegister(instructionBean.getmDestRegister(), "");
		instructionBean.setmStageAt(3, mCycleCount);
		mWaitToNextStageList.add(instructionBean);
		mMainView.setInstruction(instructionBean);
		functionalUnit.setmIsUnitBusy(false);
		functionalUnit.setmInstructionBean(null);
		functionalUnit.setmUnitForJ(null);
		functionalUnit.setmUnitForK(null);
		functionalUnit.setmIsJReady(false);
		functionalUnit.setmIsKReady(false);
		mMainView.setUnit(functionalUnit);
                updateAllUnitJKReady();
	} 
	private void updateAllUnitJKReady(){
		updateUnitJKReady(mFunctionalUnitMap.get(FunctionalUnit.ADD));
		updateUnitJKReady(mFunctionalUnitMap.get(FunctionalUnit.MUTL1));
		updateUnitJKReady(mFunctionalUnitMap.get(FunctionalUnit.MUTL2));
		updateUnitJKReady(mFunctionalUnitMap.get(FunctionalUnit.INTEGER));
		updateUnitJKReady(mFunctionalUnitMap.get(FunctionalUnit.DIVIDE));
	}
	private void updateUnitJKReady(FunctionalUnit functionalUnit){
		if(!functionalUnit.getmIsUnitBusy()){
			//unit���У����ø���
			return;
		}
		InstructionBean instructionBean=functionalUnit.getmInstructionBean();
                if(instructionBean.getmSourceRegisterJ().startsWith("F")){
                    String writeToJUnit=mRegisterMap.get(instructionBean.getmSourceRegisterJ());
                    if(writeToJUnit.equals("")){
                            functionalUnit.setmIsJReady(true);
                    }else if(!functionalUnit.getmIsJReady()){
                            functionalUnit.setmIsJReady(false);
                            functionalUnit.setmUnitForJ(writeToJUnit);
                    }
                }else{
                    functionalUnit.setmIsJReady(true);
                    functionalUnit.setmUnitForJ("");
                }
		
                if(instructionBean.getmSourceRegisterK().startsWith("F")){
                    String writeToKUnit=mRegisterMap.get(instructionBean.getmSourceRegisterK());
                    if(writeToKUnit.equals("")){
                            functionalUnit.setmIsKReady(true);
                    }else if(!functionalUnit.getmIsKReady()){
                            functionalUnit.setmIsKReady(false);
                            functionalUnit.setmUnitForK(writeToKUnit);
                    }
                }else{
                    functionalUnit.setmIsKReady(true);
                    functionalUnit.setmUnitForK("");
                }	
		mMainView.setUnit(functionalUnit);
	}

    @Override
    public void clearData() {
        if(timer!=null){
            timer.cancel();
            timer=null;
        }
        InstructionBean.INSTRUCTION_COUNT=0;
        isFinished=false;
        mInstructList.clear();
        mWaitToNextStageList.clear();
        mInstructionIssued=0;
        mCycleCount=0;
        mFunctionalUnitMap.clear();
        FunctionalUnit.UNIT_COUNT=0;
        mFunctionalUnitMap.put(FunctionalUnit.INTEGER, new FunctionalUnit(FunctionalUnit.INTEGER));
        mFunctionalUnitMap.put(FunctionalUnit.MUTL1, new FunctionalUnit(FunctionalUnit.MUTL1));
        mFunctionalUnitMap.put(FunctionalUnit.MUTL2, new FunctionalUnit(FunctionalUnit.MUTL2));
        mFunctionalUnitMap.put(FunctionalUnit.ADD, new FunctionalUnit(FunctionalUnit.ADD));
        mFunctionalUnitMap.put(FunctionalUnit.DIVIDE, new FunctionalUnit(FunctionalUnit.DIVIDE));
        mRegisterMap.clear();
        for(int i=0;i<FLOAT_REGISTER_NUM;i++){
                mRegisterMap.put("F"+i*2, "");
        }
        mMainView.resetUI();
    }

    @Override
    public void autoExcute() {
        if(timer!=null){
            return;
        }          
        timer=new Timer();
        timer.schedule(new TimerTask(){
            @Override
            public void run() {
                moveToNextCycle();
            }
        }, 0,2000);
    }

    @Override
    public void pause() {
        if(timer!=null){
            timer.cancel();
            timer=null;
        }
    }
	
}
