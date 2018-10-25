package click2call;

import javax.swing.JOptionPane;
import javax.telephony.ProviderEvent;
import com.avaya.jtapi.tsapi.adapters.ProviderListenerAdapter;

public class MyProviderListener extends ProviderListenerAdapter
{	
	Click2Call c2c;
	public MyProviderListener(Click2Call c2c)
	{
		this.c2c = c2c;
	}
	public void providerInService(ProviderEvent arg0) {
		c2c.handleProviderInService();
	}		

	public void providerShutdown(ProviderEvent arg0) {
		JOptionPane.showMessageDialog(null,"Provider ShutDown","Provider",JOptionPane.ERROR_MESSAGE);		
	}	
}
