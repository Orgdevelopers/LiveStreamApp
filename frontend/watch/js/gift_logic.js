// Gift Logic for Watch Screen
let selectedGift = null;
let userCoinBalance = 0;
let giftsList = [];

// Initialize gift functionality when DOM is ready
if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', initializeGiftDialog);
} else {
  // DOM is already loaded
  initializeGiftDialog();
}

function initializeGiftDialog() {
  const giftBtn = document.getElementById('giftBtn');
  const giftDialogOverlay = document.getElementById('giftDialogOverlay');
  const giftDialogClose = document.getElementById('giftDialogClose');
  const giftSendBtn = document.getElementById('giftSendBtn');
  const giftsGrid = document.getElementById('giftsGrid');

  // Open dialog when gift button is clicked
  giftBtn.addEventListener('click', function() {
    openGiftDialog();
  });

  // Close dialog when close button is clicked
  giftDialogClose.addEventListener('click', function() {
    closeGiftDialog();
  });

  // Close dialog when clicking outside
  giftDialogOverlay.addEventListener('click', function(e) {
    if (e.target === giftDialogOverlay) {
      closeGiftDialog();
    }
  });

  // Send gift button click handler
  giftSendBtn.addEventListener('click', function() {
    sendGift();
  });

  // Load gifts and user balance when dialog opens
  // This will be called from openGiftDialog
}

function openGiftDialog() {
  const giftDialogOverlay = document.getElementById('giftDialogOverlay');
  giftDialogOverlay.style.display = 'flex';
  
  // Reset selection
  selectedGift = null;
  updateSendButtonState();
  
  // Load gifts and user balance
  loadGifts();
  loadUserBalance();
}

function closeGiftDialog() {
  const giftDialogOverlay = document.getElementById('giftDialogOverlay');
  giftDialogOverlay.style.display = 'none';
  
  // Reset selection
  selectedGift = null;
  updateSendButtonState();
  
  // Clear selection from UI
  const giftItems = document.querySelectorAll('.gift-item');
  giftItems.forEach(item => {
    item.classList.remove('selected');
  });
}

function loadGifts() {
  const giftsGrid = document.getElementById('giftsGrid');
  giftsGrid.innerHTML = '<div class="loading">Loading gifts...</div>';

  apiRequest(
    'get',
    GIFTS_URL,
    {
        starting_point: 0,
        limit: 100
    },
    {
      onSuccess(result) {
        if (result.response && result.response.data) {
          giftsList = result.response.data;
          renderGifts(giftsList);
        } else {
          giftsGrid.innerHTML = '<div class="error">No gifts available</div>';
        }
      },
      onError(error) {
        log('Failed to load gifts:', error);
        giftsGrid.innerHTML = '<div class="error">Failed to load gifts</div>';
      }
    }
  );
}

function renderGifts(gifts) {
  const giftsGrid = document.getElementById('giftsGrid');
  giftsGrid.innerHTML = '';

  if (gifts.length === 0) {
    giftsGrid.innerHTML = '<div class="error">No gifts available</div>';
    return;
  }

  gifts.forEach(gift => {
    const giftItem = document.createElement('div');
    giftItem.classList.add('gift-item');
    giftItem.dataset.giftId = gift.id;
    
    // Convert relative URL to absolute URL using SERVER_URL
    const imageUrl = gift.image ? (gift.image.startsWith('http') ? gift.image : BASE_URL.replace(/\/$/, '') + gift.image) : '';
    
    giftItem.innerHTML = `
      <div class="gift-image-container">
        <img src="${imageUrl}" alt="${gift.name}" class="gift-image" onerror="this.src='data:image/svg+xml,%3Csvg xmlns=\'http://www.w3.org/2000/svg\' width=\'100\' height=\'100\'%3E%3Crect fill=\'%23ddd\' width=\'100\' height=\'100\'/%3E%3Ctext x=\'50%25\' y=\'50%25\' text-anchor=\'middle\' dy=\'.3em\' fill=\'%23999\'%3E🎁%3C/text%3E%3C/svg%3E'">
      </div>
      <div class="gift-name">${gift.name || 'Gift'}</div>
      <div class="gift-price">${gift.price || 0} coins</div>
    `;

    giftItem.addEventListener('click', function() {
      selectGift(gift, giftItem);
    });

    giftsGrid.appendChild(giftItem);
  });
}

function selectGift(gift, giftItemElement) {
  // Check if user has enough coins
  if (gift.price > userCoinBalance) {
    showSnackbar(false, `Not enough coins! You need ${gift.price} coins.`);
    return;
  }

  // Remove selection from all items
  const giftItems = document.querySelectorAll('.gift-item');
  giftItems.forEach(item => {
    item.classList.remove('selected');
  });

  // Add selection to clicked item
  giftItemElement.classList.add('selected');
  selectedGift = gift;

  updateSendButtonState();
}

function updateSendButtonState() {
  const giftSendBtn = document.getElementById('giftSendBtn');
  if (selectedGift) {
    giftSendBtn.disabled = false;
    giftSendBtn.textContent = `Send ${selectedGift.name} (${selectedGift.price} coins)`;
  } else {
    giftSendBtn.disabled = true;
    giftSendBtn.textContent = 'Send Gift';
  }
}

function loadUserBalance() {
  const user = getSessionUser();
  if (!user) {
    log('No user session found');
    updateBalanceDisplay(0);
    return;
  }

  // Fetch user data to get coin balance
  // Note: If coin balance is not in UserDto, this will need to be updated
  apiRequest(
    'get',
    SERVER_URL + 'users/' + user.id,
    {},
    {
      onSuccess(result) {
        if (result.response) {
          // Try to get coin balance from user data
          // If coin field doesn't exist, default to 0 or use a placeholder
          userCoinBalance = result.response.coins || result.response.coinBalance || result.response.balance || 0;
          updateBalanceDisplay(userCoinBalance);
        } else {
          updateBalanceDisplay(0);
        }
      },
      onError(error) {
        log('Failed to load user balance:', error);
        // Use session user data as fallback
        userCoinBalance = 0;
        updateBalanceDisplay(userCoinBalance);
      }
    }
  );
}

function updateBalanceDisplay(balance) {
  const balanceValue = document.getElementById('balanceValue');
  if (balanceValue) {
    balanceValue.textContent = balance;
    userCoinBalance = balance;
  }
}

function sendGift() {
  if (!selectedGift) {
    showSnackbar(false, 'Please select a gift first');
    return;
  }

  // Check if user has enough coins
  if (selectedGift.price > userCoinBalance) {
    showSnackbar(false, `Not enough coins! You need ${selectedGift.price} coins.`);
    return;
  }

  const user = getSessionUser();
  if (!user) {
    showSnackbar(false, 'Please login to send gifts');
    return;
  }

  // Get stream_id from watch.js (exposed via window.stream_id)
  // Try to get it from window or URL
  let streamId = window.stream_id || getStreamIdFromURL();
  
  if (!streamId || streamId === 0) {
    showSnackbar(false, 'Stream ID not found');
    return;
  }

  // Send gift message via WebSocket
  // Format: "🎁 [Gift Name]" or similar
  const giftMessage = `${selectedGift.name}`;
  
  // Check if stomp is available from watch.js (exposed via window.stomp)
  const stompClient = window.stomp || null;
  
  if (stompClient && stompClient.connected) {
    // Send gift as a chat message with gift info
    const giftChatMessage = {
      id: streamId,
      sender: { id: user.id },
      msg: giftMessage,
      isGift: true,
      gift: {
        id: selectedGift.id,
        // name: selectedGift.name,
        // image: selectedGift.image,
        // price: selectedGift.price,
        // isAnimated: selectedGift.isAnimated || false,
        // animation: selectedGift.animation || null
      }
    };

    stompClient.send(`/app/liveStreams/${streamId}/chat`, {}, JSON.stringify(giftChatMessage));
    
    // Update balance (subtract gift price)
    userCoinBalance -= selectedGift.price;
    updateBalanceDisplay(userCoinBalance);
    
    // Show success message
    showSnackbar(true, `Sent ${selectedGift.name}!`);
    playGiftAnimation(user, selectedGift);
    addGiftMsg("You", selectedGift.name);
    
    // Close dialog
    closeGiftDialog();
  } else {
    showSnackbar(false, 'Connection not available. Please wait...');
  }
}

function getStreamIdFromURL() {
  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  return urlParams.get('id');
}

