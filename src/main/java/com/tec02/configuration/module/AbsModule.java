/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.configuration.module;

import com.alibaba.fastjson.JSONObject;
import com.tec02.common.MyObjectMapper;
import com.tec02.configuration.Iexecute;
import lombok.NonNull;

/**
 *
 * @author Administrator
 * @param <M> model
 * @param <N>
 */
public abstract class AbsModule<M, N> implements Iexecute, IRefeshAndUpdate {

    protected final M model;
    protected final N inputModel;
    protected final AbsModuleView<M> view;

    @NonNull
    protected AbsModule(M model, N inputModel, AbsModuleView<M> view) {
        this.model = model;
        this.inputModel = inputModel;
        this.view = view;
        this.view.model = model;
    }

    @NonNull
    public void setData(JSONObject data) {
        MyObjectMapper.copy((M) MyObjectMapper.convertValue(data,
                this.inputModel.getClass()), inputModel);
    }

    @Override
    public void refesh() {
        MyObjectMapper.copy(inputModel, model);
        if (view != null) {
            view.refesh();
        }
    }

    @Override
    public void update() {
        if (view != null) {
            view.update();
        }
        MyObjectMapper.copy(model, inputModel);
    }

    public M getModel() {
        return model;
    }

    public N getInputModel() {
        return inputModel;
    }

    public AbsModuleView<M> getView() {
        return view;
    }

    public String getName() {
        return getClass().getSimpleName();
    }

}
