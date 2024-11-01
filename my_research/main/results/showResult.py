import pandas as pd
import matplotlib.pyplot as plt

def plot_csv_data(csv_filename, output_filename, i):
    
    result = pd.read_csv(csv_filename)
    result.columns = result.columns.str.strip()  # 列名の前後の空白を削除
    val_name = csv_filename[:-4]

    lam = result['Lambda']
    val = result['val']

    plt.figure(figsize=(8, 6))
    plt.scatter(lam, val, color='blue', label='val')
    plt.plot(lam, val, color='red', linestyle='-', linewidth=1, label='Trend Line')
    
    if(i==1):
        plt.xlabel('lambda')
        plt.ylabel('percent change in ' + val_name + ' / 100'+ ' : plt[-1]/plt[0] - 1')
        plt.title('Polarization with NW admin in ' + val_name)
        plt.legend()
    
    if(i==2):
        plt.xlabel('lambda')
        plt.ylabel('percent change in ' + val_name + ' : (dis[-1]/dis[0] - 1) * 100')
        plt.title('Disagreement with NW admin in ' + val_name)
        plt.legend()
    
    plt.savefig(output_filename)
    plt.close()
    



plot_csv_data('plsReddit.csv', 'plsReddit.jpg', 1)
plot_csv_data('disaggReddit.csv', 'disaggReddit.jpg', 2)
plot_csv_data('plsTwitter.csv', 'plsTwitter.jpg', 1)
plot_csv_data('disaggTwitter.csv', 'disaggTwitter.jpg', 2)