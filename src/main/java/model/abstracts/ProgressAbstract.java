package model.abstracts;

import model.analyze.beans.Progress;
import model.interfaces.IProgressBean;
import model.interfaces.IProgressModel;

public class ProgressAbstract implements IProgressModel {

    private Progress progressBean;

    /**
     * Création d'une nouvelle progression
     *
     * @param nbMaxIterate nombre maximum d'itération
     */
    protected IProgressBean createProgressBean(Integer nbMaxIterate) {
       Progress progress = new Progress(nbMaxIterate);
       this.progressBean = progress;
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
    public void resetProgress() {
        this.progressBean = null;
    }

    @Override
    public Integer getProgress() {
        if (null != progressBean) {
            return progressBean.getProgress();
        }
        return 0;
    }
}
