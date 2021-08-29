package model.abstracts;

import model.analyze.beans.Progress;
import model.interfaces.IProgressBean;
import model.interfaces.IProgressModel;

public abstract class ProgressAbstract implements IProgressModel {

    private Progress progressBean;
    private boolean treatmentIsCancelled;

    /**
     * Création d'une nouvelle progression
     *
     * @param nbMaxIterate nombre maximum d'itération
     */
    protected IProgressBean createProgressBean(Integer nbMaxIterate) {
       Progress progress = new Progress(nbMaxIterate);
       this.progressBean = progress;
       this.treatmentIsCancelled = false;
       return progress;
    }

    /**
     * Permet de se procurer le model
     * @return le model
     */
    protected IProgressModel getProgressModel() {
        return this.progressBean;
    }

    /**
     * permet de se procurer le progress bean
     * @return le progress bean
     */
    protected IProgressBean getProgressBean() {
        return this.progressBean;
    }

    /**
     * Permet de remettre la barre de progression à zéro
     */
    @Override
    public void resetProgress() {
        this.treatmentIsCancelled = false;
        this.progressBean = null;
    }

    @Override
    public Integer getProgress() {
        if (null != progressBean) {
            return progressBean.getProgress();
        }
        return 0;
    }

    @Override
    public void cancel() {
        this.treatmentIsCancelled = true;
    }

    @Override
    public boolean treatmentIsCancelled() {
        return this.treatmentIsCancelled;
    }

    @Override
    public boolean isRunning() {
        return this.progressBean != null;
    }
}
