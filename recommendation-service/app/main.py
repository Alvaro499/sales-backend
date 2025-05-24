from fastapi import FastAPI
from app.association import get_association_recommendations
from app.content_based import get_content_based_recommendations

app = FastAPI()

@app.get("/recommendations/association/{user_id}")
def association_recommendations(user_id: int):
    return get_association_recommendations(user_id)

@app.get("/recommendations/content/{product_id}")
def content_recommendations(product_id: int):
    return get_content_based_recommendations(product_id)
