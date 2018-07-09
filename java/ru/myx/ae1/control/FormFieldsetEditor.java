package ru.myx.ae1.control;

import java.util.Collections;
import java.util.function.Function;

import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseFunctionActAbstract;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.AbstractForm;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.control.field.ControlField;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.help.Convert;

/*
 * Created on 26.04.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
final class FormFieldsetEditor extends AbstractForm<FormFieldsetEditor> {

	private static final ControlCommand<?> CMD_APPLY = Control.createCommand(
			"apply", //
			MultivariantString.getString(
					"Apply", //
					Collections.singletonMap(
							"ru", //
							"Применить")))//
			.setCommandIcon("command-apply");

	private static final ControlCommand<?> CMD_EDIT = Control.createCommand(
			"edit", //
			MultivariantString.getString(
					"Edit", //
					Collections.singletonMap(
							"ru", //
							"Редактирование")))//
			.setCommandIcon("command-edit");

	private static final ControlCommand<?> CMD_FORM = Control.createCommand("form", MultivariantString.getString("Form", Collections.singletonMap("ru", "Форма")))
			.setCommandIcon("command-view");

	private static final ControlCommand<?> CMD_SAVE = Control.createCommand("save", " OK ").setCommandIcon("command-save");

	private static final ControlCommand<?> CMD_SOURCE = Control.createCommand("source", MultivariantString.getString("Source", Collections.singletonMap("ru", "Код")))
			.setCommandIcon("command-view");

	private static final ControlFieldset<?> FSET_LIST = ControlFieldset.createFieldset()
			.addField(ControlFieldFactory.createFieldString("title", MultivariantString.getString("Title", Collections.singletonMap("ru", "Заголовок")), ""))
			.addField(ControlFieldFactory.createFieldString("class", MultivariantString.getString("Class", Collections.singletonMap("ru", "Класс")), ""))
			.addField(ControlFieldFactory.createFieldString("type", MultivariantString.getString("Type", Collections.singletonMap("ru", "Тип")), ""))
			.addField(ControlFieldFactory.createFieldString("variant", MultivariantString.getString("Variant", Collections.singletonMap("ru", "Вариант")), ""));

	private static final ControlFieldset<?> FSET_SOURCE = ControlFieldset.createFieldset()
			//
			.addField(
					ControlFieldFactory.createFieldString(
							"source", //
							MultivariantString.getString(
									"Source", //
									Collections.singletonMap(
											"ru", //
											"Код")),
							"") //
							.setFieldType("text") //
							.setFieldVariant("bigtext") //
	// .setConstant()
	);

	private static final int MD_EDIT = 0;

	private static final int MD_FORM = 1;

	private static final int MD_SOURCE = 2;

	private final ControlFieldset<?> fieldset;

	private int mode;

	private final Function<ControlFieldset<?>, Object> target;

	FormFieldsetEditor(final Object title, final ControlFieldset<?> fieldset, final Function<ControlFieldset<?>, Object> target) {

		this.fieldset = ControlFieldset.createFieldset();
		for (int i = 0; i < fieldset.size(); ++i) {
			this.fieldset.addField(fieldset.get(i));
		}
		this.target = target;
		this.mode = FormFieldsetEditor.MD_EDIT;
		this.setData(
				new BaseNativeObject()//
						.putAppend("fieldset", this.fieldset) //
		);
		this.setAttributeIntern("id", "fieldset_editor");
		this.setAttributeIntern("title", Base.forUnknown(title));
		this.recalculate();
	}

	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) {

		if (command == FormFieldsetEditor.CMD_EDIT) {
			this.mode = FormFieldsetEditor.MD_EDIT;
			this.setData(
					new BaseNativeObject()//
							.putAppend("fieldset", this.fieldset) //
			);
			return this;
		}
		if (command == FormFieldsetEditor.CMD_FORM) {
			this.mode = FormFieldsetEditor.MD_FORM;
			this.setData(null);
			return this;
		}
		if (command == FormFieldsetEditor.CMD_SOURCE) {
			this.mode = FormFieldsetEditor.MD_SOURCE;
			final ControlFieldset<?> fieldset = ControlFieldset.createFieldset();
			fieldset.addFields(this.fieldset);
			this.setData(
					new BaseNativeObject()//
							.putAppend("source", ControlFieldset.serializeFieldset(fieldset, true))//
			);
			return this;
		}
		if (command == FormFieldsetEditor.CMD_SAVE || command == FormFieldsetEditor.CMD_APPLY) {
			if (this.mode == FormFieldsetEditor.MD_SOURCE) {
				final ControlFieldset<?> fieldset = ControlFieldset.materializeFieldset(Base.getString(this.getData() //
						, "source", "").trim() //
				);
				this.fieldset.clear();
				this.fieldset.addFields(fieldset);
				this.fieldset.setAttributes(fieldset.getAttributes());
			}
			final ControlFieldset<?> fieldset = ControlFieldset.createFieldset();
			try {
				fieldset.addFields(this.fieldset);
				final Object result = this.target.apply(fieldset);
				return command == FormFieldsetEditor.CMD_SAVE
					? result
					: result == null
						? this
						: result;
			} catch (final RuntimeException e) {
				throw e;
			} catch (final Throwable e) {
				throw new RuntimeException(e);
			}
		}
		throw new IllegalArgumentException("Unknown command: " + command.getKey());
	}

	@Override
	public ControlCommandset getCommands() {

		final ControlCommandset result = Control.createOptions();
		switch (this.mode) {
			case FormFieldsetEditor.MD_EDIT :
				result.add(FormFieldsetEditor.CMD_FORM);
				result.add(FormFieldsetEditor.CMD_SOURCE);
				break;
			case FormFieldsetEditor.MD_FORM :
				result.add(FormFieldsetEditor.CMD_EDIT);
				result.add(FormFieldsetEditor.CMD_SOURCE);
				break;
			case FormFieldsetEditor.MD_SOURCE :
				result.add(FormFieldsetEditor.CMD_EDIT);
				result.add(FormFieldsetEditor.CMD_FORM);
				break;
			default :
		}
		if (this.target != null) {
			result.add(FormFieldsetEditor.CMD_SAVE);
			result.add(FormFieldsetEditor.CMD_APPLY);
		}
		return result;
	}

	@Override
	public ControlFieldset<?> getFieldset() {

		switch (this.mode) {
			case FormFieldsetEditor.MD_FORM :
				return this.fieldset;
			case FormFieldsetEditor.MD_SOURCE :
				return FormFieldsetEditor.FSET_SOURCE;
			case FormFieldsetEditor.MD_EDIT :
			default : {
				final ControlField fieldsetField = Control.createFieldList(
						"fieldset", //
						MultivariantString.getString(
								"Fieldset", //
								Collections.singletonMap(
										"ru", //
										"Список полей")),
						this.fieldset)//
						.setAttribute(
								"content_fieldset", //
								FormFieldsetEditor.FSET_LIST);
				if (this.target == null) {
					fieldsetField.setConstant();
				} else {
					class ContentHandler extends BaseFunctionActAbstract<ControlFieldset<?>, ContainerFieldset> {

						@SuppressWarnings("unchecked")
						ContentHandler() {

							super((Class<ControlFieldset<?>>) Convert.Any.toAny(ControlFieldset.class), ContainerFieldset.class);
						}

						@Override
						public ContainerFieldset apply(final ControlFieldset<?> argument) {

							return new ContainerFieldset(argument);
						}
					}
					fieldsetField.setAttribute("content_handler", new ContentHandler());
				}
				return ControlFieldset.createFieldset().addField(fieldsetField);
			}
		}
	}
}
