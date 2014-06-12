package com.fortysevendeg.android.swipelistview;

import android.graphics.Rect;
import android.os.Handler;
import android.view.*;
import android.widget.AbsListView;
import android.widget.ListView;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.nineoldandroids.view.ViewHelper.setTranslationX;
import static com.nineoldandroids.view.ViewPropertyAnimator.animate;

/**
 * Touch listener impl for the SwipeListView
 */
public class SwipeListViewTouchListener implements View.OnTouchListener {
	private boolean swipeClosesAllItemsWhenListMoves = true;

	private int swipeFrontView = 0;
	private int swipeBackView = 0;

	private Rect rect = new Rect();

	// Cached ViewConfiguration and system-wide constant values
	private int slop;
	private int minFlingVelocity;
	private int maxFlingVelocity;
	private long configShortAnimationTime;
	private long animationTime;
	private int swipeCurrentAction;
	private float rightOffset = 0;

	// Fixed properties
	private SwipeListView swipeListView;
	private int viewWidth = 1; // 1 and not 0 to prevent dividing by zero
	private int viewWidthHalf = 1;

	private float downX;
	private boolean swiping;
	private VelocityTracker velocityTracker;
	private int downPosition;
	private View frontView;
	private boolean paused;

	private List<Boolean> opened = new ArrayList<Boolean>();
	private boolean listViewMoving;

	/**
	 * Constructor
	 * @param swipeListView SwipeListView
	 * @param swipeFrontView front view Identifier
	 * @param swipeBackView back view Identifier
	 */
	public SwipeListViewTouchListener(SwipeListView swipeListView, int swipeFrontView, int swipeBackView) {
		this.swipeFrontView = swipeFrontView;
		this.swipeBackView = swipeBackView;
		ViewConfiguration vc = ViewConfiguration.get(swipeListView.getContext());
		slop = vc.getScaledTouchSlop();
		minFlingVelocity = vc.getScaledMinimumFlingVelocity();
		maxFlingVelocity = vc.getScaledMaximumFlingVelocity();
		configShortAnimationTime = swipeListView.getContext().getResources().getInteger(android.R.integer.config_shortAnimTime);
		animationTime = configShortAnimationTime;
		this.swipeListView = swipeListView;
	}

	/**
	 * Sets current item's front view
	 * @param frontView Front view
	 */
	private void setFrontView(View frontView) {
		this.frontView = frontView;
		frontView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				swipeListView.onClickFrontView(downPosition);
			}
		});
	}

	/**
	 * Set current item's back view
	 * @param backView
	 */
	private void setBackView(View backView) {
		backView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				swipeListView.onClickBackView(downPosition);
			}
		});
	}

	/**
	 * @return true if the list is in motion
	 */
	public boolean isListViewMoving() {
		return listViewMoving;
	}

	/**
	 * Sets animation time when the user drops the cell
	 *
	 * @param animationTime milliseconds
	 */
	public void setAnimationTime(long animationTime) {
		if (animationTime > 0) {
			this.animationTime = animationTime;
		} else {
			this.animationTime = configShortAnimationTime;
		}
	}

	/**
	 * Sets the right offset
	 *
	 * @param rightOffset Offset
	 */
	public void setRightOffset(float rightOffset) {
		this.rightOffset = rightOffset;
	}

	/**
	 * Set if all item opened will be close when the user move ListView
	 *
	 * @param swipeClosesAllItemsWhenListMoves
	 */
	public void setSwipeClosesAllItemsWhenListMoves(boolean swipeClosesAllItemsWhenListMoves) {
		this.swipeClosesAllItemsWhenListMoves = swipeClosesAllItemsWhenListMoves;
	}

	/**
	 * Adds new items when adapter is modified
	 */
	public void resetItems() {
		if (swipeListView.getAdapter() != null) {
			int count = swipeListView.getAdapter().getCount();
			for (int i = opened.size(); i <= count; i++) {
				opened.add(false);
			}
		}
	}

	/**
	 * Open item
	 * @param position Position of list
	 */
	protected void openAnimate(int position) {
		openAnimate(swipeListView.getChildAt(position - swipeListView.getFirstVisiblePosition()).findViewById(swipeFrontView), position);
	}

	/**
	 * Close item
	 * @param position Position of list
	 */
	protected void closeAnimate(int position) {
		closeAnimate(swipeListView.getChildAt(position - swipeListView.getFirstVisiblePosition()).findViewById(swipeFrontView), position);
	}

	/**
	 * Open item
	 * @param view affected view
	 * @param position Position of list
	 */
	private void openAnimate(View view, int position) {
		if (!opened.get(position)) {
			generateRevealAnimate(view, true, true, position);
		}
	}

	/**
	 * Close item
	 * @param view affected view
	 * @param position Position of list
	 */
	private void closeAnimate(View view, int position) {
		if (opened.get(position)) {
			generateRevealAnimate(view, true, false, position);
		}
	}

	/**
	 * Create animation
	 * @param view affected view
	 * @param swap If state should change. If "false" returns to the original position
	 * @param swapRight If swap is true, this parameter tells if move is to the right or left
	 * @param position Position of list
	 */
	private void generateAnimate(final View view, final boolean swap, final boolean swapRight, final int position) {
		generateRevealAnimate(view, swap, swapRight, position);
	}

	/**
	 * Create reveal animation
	 * @param view affected view
	 * @param swap If will change state. If "false" returns to the original position
	 * @param swapRight If swap is true, this parameter tells if movement is toward right or left
	 * @param position list position
	 */
	private void generateRevealAnimate(final View view, final boolean swap, final boolean swapRight, final int position) {
		int moveTo = 0;
		if (opened.get(position)) {
			if (!swap) {
				moveTo = (int) (viewWidth - rightOffset);
			}
		} else {
			if (swap) {
				moveTo = swapRight ? (int) (viewWidth - rightOffset) : (int) -viewWidth;
			}
		}

		animate(view)
		.translationX(moveTo)
		.setDuration(animationTime)
		.setListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				swipeListView.resetScrolling();
				if (swap) {
					boolean aux = !opened.get(position);
					opened.set(position, aux);
					if (aux) {
						swipeListView.onOpened(position, swapRight);
					} else {
						swipeListView.onClosed(position, true);
					}
				}
			}
		});
	}

	/**
	 * Set enabled
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) {
		paused = !enabled;
	}

	/**
	 * Return ScrollListener for ListView
	 * @return OnScrollListener
	 */
	public AbsListView.OnScrollListener makeScrollListener() {
		return new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView absListView, int scrollState) {
				setEnabled(scrollState != AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
				if (swipeClosesAllItemsWhenListMoves && scrollState == SCROLL_STATE_TOUCH_SCROLL) {
					closeOpenedItems(-1);
				}

				if (scrollState==SCROLL_STATE_TOUCH_SCROLL) {
					listViewMoving = true;
					setEnabled(false);
				}

				if (scrollState != AbsListView.OnScrollListener.SCROLL_STATE_FLING && scrollState != SCROLL_STATE_TOUCH_SCROLL) {
					listViewMoving = false;
					swipeListView.resetScrolling();
					new Handler().postDelayed(new Runnable() {
						public void run() {
							setEnabled(true);
						}
					}, 500);
				}
			}

			@Override
			public void onScroll(AbsListView absListView, int i, int i1, int i2) {
			}
		};
	}

	/**
	 * Close all opened items
	 */
	void closeOpenedItems(int excludePostion) {
		if (opened != null) {
			int start = swipeListView.getFirstVisiblePosition();
			int end = swipeListView.getLastVisiblePosition();
			for (int i = start; i <= end; i++) {
				if (excludePostion != -1 && excludePostion == i) {
					continue;
				}
				
				if (opened.get(i)) {
					closeAnimate(swipeListView.getChildAt(i - start).findViewById(swipeFrontView), i);
				}
			}
		}

	}

	/**
	 * @see View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
	 */
	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
		if (viewWidth < 2) {
			viewWidth = swipeListView.getWidth();
			viewWidthHalf = (int)(viewWidth / 2);
		}

		switch (motionEvent.getActionMasked()) {
		case MotionEvent.ACTION_DOWN: {
			if (paused) {
				return false;
			}

			swipeCurrentAction = SwipeListView.SWIPE_ACTION_NONE;

			int childCount = swipeListView.getChildCount();
			int[] listViewCoords = new int[2];
			swipeListView.getLocationOnScreen(listViewCoords);
			int x = (int) motionEvent.getRawX() - listViewCoords[0];
			int y = (int) motionEvent.getRawY() - listViewCoords[1];
			View child;
			for (int i = 0; i < childCount; i++) {
				child = swipeListView.getChildAt(i);
				child.getHitRect(rect);
				if (rect.contains(x, y) && swipeListView.getAdapter().isEnabled(swipeListView.getFirstVisiblePosition() + i)) {
					setFrontView(child.findViewById(swipeFrontView));
					downX = motionEvent.getRawX();
					downPosition = swipeListView.getPositionForView(child);

					frontView.setClickable(!opened.get(downPosition));

					velocityTracker = VelocityTracker.obtain();
					velocityTracker.addMovement(motionEvent);
					if (swipeBackView > 0) {
						setBackView(child.findViewById(swipeBackView));
					}

					break;
				}
			}

			view.onTouchEvent(motionEvent);

			return true;
		}

		case MotionEvent.ACTION_UP: {
			if (velocityTracker == null || !swiping) {
				break;
			}

			float deltaX = motionEvent.getRawX() - downX;
			velocityTracker.addMovement(motionEvent);
			velocityTracker.computeCurrentVelocity(1000);
			float velocityX = Math.abs(velocityTracker.getXVelocity());
			if (!opened.get(downPosition)) {
				if (velocityTracker.getXVelocity() < 0) {
					velocityX = 0;
				}
			}

			float velocityY = Math.abs(velocityTracker.getYVelocity());
			boolean swap = false;
			boolean swapRight = false;
			if (minFlingVelocity <= velocityX && velocityX <= maxFlingVelocity && velocityY < velocityX) {
				swapRight = velocityTracker.getXVelocity() > 0;
				if (opened.get(downPosition) && swapRight) {
					swap = false;
				} else if (!opened.get(downPosition) && !swapRight) {
					swap = false;
				} else {
					swap = true;
				}
			} else if (Math.abs(motionEvent.getX()) < viewWidthHalf) {
				swap = false;
				swapRight = deltaX > 0;
			} else if (Math.abs(motionEvent.getX()) > viewWidthHalf) {
				swap = false;
				swapRight = deltaX < 0;
			}

			generateAnimate(frontView, swap, swapRight, downPosition);

			velocityTracker.recycle();
			velocityTracker = null;
			downX = 0;

			// change clickable front view
			if (swap) {
				frontView.setClickable(opened.get(downPosition));
			}

			frontView = null;
			this.downPosition = ListView.INVALID_POSITION;
			swiping = false;
			break;
		}

		case MotionEvent.ACTION_MOVE: {
			if (velocityTracker == null || paused) {
				break;
			}

			velocityTracker.addMovement(motionEvent);
			velocityTracker.computeCurrentVelocity(1000);
			float velocityX = Math.abs(velocityTracker.getXVelocity());
			float velocityY = Math.abs(velocityTracker.getYVelocity());

			float deltaX = motionEvent.getRawX() - downX;
			float deltaMode = Math.abs(deltaX);

			if (opened.get(downPosition)) {
				if (deltaX > 0) {
					deltaMode = 0;
				}
			} else {
				if (deltaX < 0) {
					deltaMode = 0;
				}
			}

			if (deltaMode > slop && swipeCurrentAction == SwipeListView.SWIPE_ACTION_NONE && velocityY < velocityX) {
				swiping = true;
				boolean swipingRight = (deltaX > 0);
				if (opened.get(downPosition)) {
					swipeListView.onStartClose(downPosition, swipingRight);
					swipeCurrentAction = SwipeListView.SWIPE_ACTION_REVEAL;
				} else {
					swipeCurrentAction = SwipeListView.SWIPE_ACTION_REVEAL;
					swipeListView.onStartOpen(downPosition, swipeCurrentAction, swipingRight);
				}

				swipeListView.requestDisallowInterceptTouchEvent(true);
				MotionEvent cancelEvent = MotionEvent.obtain(motionEvent);
				cancelEvent.setAction(MotionEvent.ACTION_CANCEL | (motionEvent.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
				swipeListView.onTouchEvent(cancelEvent);
			}

			if (swiping) {
				if (opened.get(downPosition)) {
					deltaX += viewWidth - rightOffset;
				}

				move(deltaX);

				return true;
			}

			break;
		}
		}

		return false;
	}

	/**
	 * Moves the view
	 * @param deltaX delta
	 */
	public void move(float deltaX) {
		if (deltaX < 0)
			deltaX = 0;
		
		swipeListView.onMove(downPosition, deltaX);

		setTranslationX(frontView, deltaX);
	}
}
