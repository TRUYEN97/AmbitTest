/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tec02.configuration.module;

import com.alibaba.fastjson2.JSONObject;
import com.tec02.common.MyObjectMapper;
import com.tec02.configuration.Iexecute;
import lombok.NonNull;

/**
 *
 * @author Administrator
 * @param <M> model
 * @param <N> input model
 * @param <V> View
 */
public abstract class AbsModule<M, N, V extends AbsModuleView<M>> implements Iexecute, IRefeshAndUpdate {

    protected final M model;
    protected final N inputModel;
    protected final V view;

    @NonNull
    protected AbsModule(M model, N inputModel, V module) {
        this.model = model;
        this.inputModel = inputModel;
        this.view = module;
        this.view.model = model;
    }

    @NonNull
    public void setData(JSONObject data) {
        MyObjectMapper.copy(data, inputModel);
        MyObjectMapper.copy(inputModel, model);
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
