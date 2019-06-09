package org.vieuxchameau.sandbox.exchangesrates;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.StringJoiner;

@XmlRootElement
public class ExchangesRatesRequestMessage {
    private String baseCurrency;
    private List<String> currencies;

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(final String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public List<String> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(final List<String> currencies) {
        this.currencies = currencies;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ExchangesRatesRequestMessage.class.getSimpleName() + "[", "]")
                .add("baseCurrency='" + baseCurrency + "'")
                .add("currencies=" + currencies)
                .toString();
    }
}
