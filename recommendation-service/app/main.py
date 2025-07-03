from fastapi import FastAPI
from app.association import get_association_recommendations
from app.content_based import get_content_based_recommendations
from app.schemas import RecommendationResponse
from app.models import Base
from app.database import engine
from uuid import UUID
import threading
from app.consumer import run_consumer

app = FastAPI()

@app.on_event("startup")
def start_consumer():
    thread = threading.Thread(target=run_consumer, daemon=True)
    thread.start()

@app.get("/recommendations/association/{user_id}", response_model=RecommendationResponse)
def association_recommendations(user_id: UUID):
    return get_association_recommendations(user_id)

@app.get("/recommendations/content/{product_id}", response_model=RecommendationResponse)
def content_recommendations(product_id: UUID):
    return get_content_based_recommendations(product_id)

Base.metadata.create_all(bind=engine)