import pandas as pd
import matplotlib.pyplot as plt

def plot_csv_data(csv_filename, output_filename):
    
    result = pd.read_csv(csv_filename)
    val_name = csv_filename[:-4]

    lam = result['Lambda']
    val = result['val']

    plt.figure(figsize=(8, 6))
    plt.scatter(lam, val, color='blue', label='val')
    plt.plot(lam, val, color='red', linestyle='-', linewidth=1, label='Trend Line')
    
    plt.xlabel('lambda')
    plt.ylabel('percent change in ' + val_name + ' / 100')
    plt.title('Polarization with NW admin in ' + val_name)
    plt.legend()
    
    plt.show()
    
    plt.savefig(output_filename)


plot_csv_data('plsReddit.csv', 'plsReddit.pdf')
plot_csv_data('disaggReddit.csv', 'disaggReddit.pdf')
plot_csv_data('plsTwitter.csv', 'plsTwitter.pdf')
plot_csv_data('disaggTwitter.csv', 'disaggTwitter.pdf')
