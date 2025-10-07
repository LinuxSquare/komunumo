/*
 * Komunumo - Open Source Community Manager
 * Copyright (C) Marcus Fihlon and the individual contributors to Komunumo.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package app.komunumo.ui.views.admin.config;

import app.komunumo.data.dto.ConfigurationSetting;
import app.komunumo.data.service.ConfigurationService;
import app.komunumo.ui.TranslationProvider;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

final class ConfigurationEditorComponent extends VerticalLayout {

    private final @NotNull ConfigurationService configurationService;
    private final @NotNull ConfigurationSetting configurationSetting;

    ConfigurationEditorComponent(final @NotNull ConfigurationService configurationService,
                                 final @NotNull ConfigurationSetting configurationSetting) {
        super();
        this.configurationService = configurationService;
        this.configurationSetting = configurationSetting;
        addClassName("configuration-editor-component");
        add(createComponent());
    }

    private @NotNull Component createComponent() {
        if (configurationSetting.isLanguageDependent()) {
            final var container = new VerticalLayout();
            container.addClassName("language-group");
            for (final var locale : TranslationProvider.getSupportedLocales()) {
                container.add(createComponent(locale));
            }
            return container;
        }

        return createComponent(null);
    }

    private @NotNull Component createComponent(final @Nullable Locale locale) {
        final var defaultValue = configurationSetting.defaultValue();
        final var label = getTranslation("ui.views.admin.config.ConfigurationEditorView.label." + configurationSetting.setting());
        final var localizedLabel = locale != null ? label + " (" + locale.getDisplayLanguage(locale) + ")" : label;
        final var actualValue = configurationService.getConfiguration(configurationSetting, locale);
        final var isDefaultValue = defaultValue.equals(actualValue);

        final var textField = new TextField();
        textField.setValueChangeMode(ValueChangeMode.EAGER);
        textField.setLabel(localizedLabel);
        textField.setPlaceholder(defaultValue);
        textField.setWidthFull();

        if (!isDefaultValue) {
            textField.setValue(actualValue);
        }

        final var defaultButton = new Button(new Icon(VaadinIcon.TRASH));
        defaultButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
        defaultButton.setAriaLabel("Reset setting to default value");
        defaultButton.setTooltipText("Reset setting to default value");
        defaultButton.setEnabled(!isDefaultValue);
        defaultButton.addClickListener(_ -> textField.setValue(defaultValue));

        final var resetButton = new Button(new Icon(VaadinIcon.REFRESH));
        resetButton.addThemeVariants(ButtonVariant.LUMO_ICON);
        resetButton.setAriaLabel("Reset setting to stored value");
        resetButton.setTooltipText("Reset setting to stored value");
        resetButton.setEnabled(false);
        resetButton.addClickListener(_ -> textField.setValue(actualValue));

        final var saveButton = new Button(new Icon(VaadinIcon.CHECK));
        saveButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SUCCESS);
        saveButton.setAriaLabel("Save and activate setting");
        saveButton.setTooltipText("Save and activate setting");
        saveButton.setEnabled(false);
        saveButton.addClickListener(_ -> {
            final var newValue = textField.getValue().trim();
            if (defaultValue.equals(newValue)) {
                configurationService.deleteConfiguration(configurationSetting, locale);
            } else {
                configurationService.setConfiguration(configurationSetting, locale, newValue);
            }

            saveButton.setEnabled(false);
            resetButton.setEnabled(false);
            defaultButton.setEnabled(!defaultValue.equals(textField.getValue()));
        });

        textField.addValueChangeListener(valueChangeEvent -> {
            final var newValue = valueChangeEvent.getValue();
            defaultButton.setEnabled(!defaultValue.equals(newValue));
            resetButton.setEnabled(!isDefaultValue && !actualValue.equals(newValue));
            saveButton.setEnabled(!actualValue.equals(newValue));
        });

        final var buttonBar = new HorizontalLayout(defaultButton, resetButton, saveButton);
        buttonBar.addClassName("button-bar");

        final var component = new VerticalLayout(textField, buttonBar);
        component.addClassName("configuration-setting-field");
        return component;
    }

}
