import pandas as pd
import matplotlib.pyplot as plt

def plot_csv_data(csv_filename, output_filename, plain_filename, i):
    
    result = pd.read_csv(csv_filename)
    result.columns = result.columns.str.strip()  # 列名の前後の空白を削除
    val_name = csv_filename[:-4]

    lam = result['Lambda']
    val = result['val']

    plt.figure(figsize=(8, 6))
    plt.scatter(lam, val, color='red', label='new result')
    plt.plot(lam, val, color='red', linestyle='-', linewidth=1)
    
    plain_result = pd.read_csv(plain_filename)
    plain_result.columns = plain_result.columns.str.strip()
    plain_lam = plain_result['Lambda']
    plain_val = plain_result['val']
    
    plt.scatter(plain_lam, plain_val, color='green', label='plain result', alpha=0.3)
    plt.plot(plain_lam, plain_val, color='green', alpha=0.3)
    
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
    if(i==3):
        plt.xlabel('lambda')
        plt.ylabel('final extremist ratio in  ' + val_name + ' : gppls[-1] / gppls[0]')
        plt.title('GroupPolarization with NW admin in ' + val_name)
        plt.legend()
    if(i==4):
        plt.xlabel('lambda')
        plt.ylabel('final satisfaction in ' + val_name + ' : stfs[-1]')
        plt.title('User Satisfaction with NW admin in ' + val_name)
        plt.legend()
    if(i==5):
        plt.xlabel('lambda')
        plt.ylabel('final diversity in ' + val_name + ' : dvs[-1]')
        plt.title('Diversity with NW admin in ' + val_name)
        plt.legend()
    
    plt.savefig(output_filename)
    plt.close()
    



plot_csv_data('plsReddit.csv', 'plsReddit.jpg', 'plsReddit0.csv', 1)
plot_csv_data('disaggReddit.csv', 'disaggReddit.jpg', 'disaggReddit0.csv', 2)
plot_csv_data('gpplsReddit.csv', 'gpplsReddit.jpg', 'gpplsReddit0.csv', 3)
plot_csv_data('stfsReddit.csv', 'stfsReddit.jpg', 'stfsReddit0.csv', 4)
plot_csv_data('dvsReddit.csv', 'dvsReddit.jpg', 'dvsReddit0.csv', 5)

plot_csv_data('plsTwitter.csv', 'plsTwitter.jpg', 'plsTwitter0.csv', 1)
plot_csv_data('disaggTwitter.csv', 'disaggTwitter.jpg', 'disaggTwitter0.csv', 2)
plot_csv_data('gpplsTwitter.csv', 'gpplsTwitter.jpg', 'gpplsTwitter0.csv', 3)
plot_csv_data('stfsTwitter.csv', 'stfsTwitter.jpg', 'stfsTwitter0.csv', 4)
plot_csv_data('dvsTwitter.csv', 'dvsTwitter.jpg', 'dvsTwitter0.csv', 5)